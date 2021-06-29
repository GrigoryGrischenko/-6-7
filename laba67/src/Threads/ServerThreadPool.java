package Threads;

import MethodsClasses.ServerMethodsCheck;
import Other.Packet;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class ServerThreadPool extends ServerThread {
    static Connection connection;
    private static final int MAX_THREADS = 5;
    private ReadingPool readingPool;
    private WritingPool writingPool;

    public ServerThreadPool(Connection connection, String host, int port) {
        super(host, port);
        this.connection = connection;
        readingPool = new ReadingPool(MAX_THREADS);
        writingPool = new WritingPool(MAX_THREADS);
    }

    @Override
    protected void read(SelectionKey key) throws IOException, ClassNotFoundException {
        ReadingThread reader = readingPool.getThread();
        if (reader == null) {
            return;
        }
        reader.serviceChannel(key);
    }
    @Override
    protected void write(SelectionKey key) throws IOException {
        WritingThread writer = writingPool.getThread();
        if (writer == null) {
            return;
        }
        writer.serviceChannel(key);
    }

    private class ReadingPool {
        List idle = new LinkedList();

        ReadingPool(int poolSize) {
            for (int i = 0; i < poolSize; i++) {
                ReadingThread thread = new ReadingThread(this);
                // Set thread name for debugging. Start it.
                thread.setName("Reader" + (i + 1));
                thread.start();
                idle.add(thread);
            }
        }
        ReadingThread getThread() {
            ReadingThread reader = null;
            synchronized (idle) {
                if (idle.size() > 0) {
                    reader = (ReadingThread) idle.remove(0);
                }
            }
            return (reader);
        }
        void returnReader(ReadingThread reader) {
            synchronized (idle) {
                idle.add(reader);
            }
        }
    }
    private class WritingPool {
        List idle = new LinkedList();

        WritingPool(int poolSize) {
            for (int i = 0; i < poolSize; i++) {
                WritingThread thread = new WritingThread(this, connection);
                thread.setName("Writer" + (i + 1));
                thread.start();
                idle.add(thread);
            }
        }
        WritingThread getThread() {
            WritingThread writer = null;
            synchronized (idle) {
                if (idle.size() > 0) {
                    writer = (WritingThread) idle.remove(0);
                }
            }
            return (writer);
        }
        void returnWriter(WritingThread writer) {
            synchronized (idle) {
                idle.add(writer);
            }
        }
    }
    private class ReadingThread extends Thread {
        private ByteBuffer buffer = ByteBuffer.allocate(1024);
        private ReadingPool pool;
        private SelectionKey key;

        ReadingThread(ReadingPool pool) {
            this.pool = pool;
        }

        public synchronized void run() {
            while (true) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    this.interrupted();
                }
                if (key == null) {
                    continue;
                }
                try {
                    drainChannel(key);
                } catch (Exception e) {
                    try {
                        key.channel().close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    key.selector().wakeup();
                }
                key = null;
                this.pool.returnReader(this);
            }
        }
        synchronized void serviceChannel(SelectionKey key) {
            this.key = key;
            key.interestOps(key.interestOps() & (~SelectionKey.OP_READ));
            this.notify();
        }
        void drainChannel(SelectionKey key) throws IOException{
            SocketChannel channel = (SocketChannel) key.channel();
            try {
                buffer.clear();
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                channel.read(buffer);
                key.attach(Packet.deserialize(buffer.array()));
                key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
                key.selector().wakeup();
            } catch (ClassNotFoundException e) {
                ByteBuffer buffer = ByteBuffer.wrap(new Packet(null, "Нет нужного класса. Дальнейшая работа невозмодна", null).serialize());
                channel.write(buffer);
            }
        }
    }
    private class WritingThread extends Thread {
        private ByteBuffer buffer = ByteBuffer.allocate(1024);
        private WritingPool pool;
        private SelectionKey key;
        private Connection connection;

        WritingThread(WritingPool pool, Connection connection) {
            this.pool = pool;
            this.connection = connection;
        }

        public synchronized void run() {
            while (true) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    this.interrupted();
                }
                if (key == null) {
                    continue;
                }
                try {
                    drainChannel(key);
                } catch (Exception e) {
                    try {
                        key.channel().close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    key.selector().wakeup();
                }
                key = null;
                this.pool.returnWriter(this);
            }
        }
        synchronized void serviceChannel(SelectionKey key) {
            this.key = key;
            key.interestOps(key.interestOps() & (~SelectionKey.OP_WRITE));
            this.notify();
        }
        void drainChannel(SelectionKey key) throws IOException{
            SocketChannel channel = (SocketChannel) key.channel();
            try {
                ServerMethodsCheck serverMethodsCheck = new ServerMethodsCheck();
                Packet packet = serverMethodsCheck.check((Packet) key.attachment(), connection);

                ByteBuffer buffer = ByteBuffer.wrap(packet.serialize());

                //buffer.clear();
                channel.write(buffer);
                key.interestOps(key.interestOps() | SelectionKey.OP_READ);
                key.selector().wakeup();
            } catch (SQLException e) {
                e.printStackTrace();
                ByteBuffer buffer = ByteBuffer.wrap(new Packet(null, "База данных не доступна", null).serialize());
                channel.write(buffer);
            } catch (NoSuchAlgorithmException e) {
                ByteBuffer buffer = ByteBuffer.wrap(new Packet(null, "Нет алгоритма хеширования. Дальнейшая работа невозмодна", null).serialize());
                channel.write(buffer);
            }
        }
    }
}