package Threads;

import Other.Packet;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

public class ServerThread extends  Thread {
    String host;
    int port;
    long TIMEOUT = 100000000;
    private Packet packageReceived;

    public ServerThread(String host, int port) {
        this.host = host;
        this.port = port;
        start();
    }

    public void run() {
        try {
            ServerSocketChannel serverChannel = ServerSocketChannel.open();
            serverChannel.configureBlocking(false);
            serverChannel.socket().bind(new InetSocketAddress(host, port));
            Selector selector = Selector.open();

            serverChannel.register(selector, SelectionKey.OP_ACCEPT);


            while (!Thread.currentThread().isInterrupted()) {

                selector.select(TIMEOUT);
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();
                    if (!key.isValid()) {
                        continue;
                    }
                    if (key.isAcceptable()) {
                        accept(key);
                    }
                    if (key.isWritable()) {
                        write(key);
                    }
                    if (key.isReadable()) {
                        read(key);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void accept(SelectionKey key) throws IOException {
        SocketChannel channel = ((ServerSocketChannel) key.channel()).accept();
        channel.configureBlocking(false);
        channel.register(key.selector(), SelectionKey.OP_READ);
    }
    protected void read(SelectionKey key) throws IOException, ClassNotFoundException {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        channel.read(buffer);
        packageReceived = Packet.deserialize(buffer.array());
        channel.register(key.selector(), SelectionKey.OP_WRITE);
    }
    protected void write(SelectionKey key) throws IOException {
        System.out.println("Написал");
        SocketChannel channel = ((SocketChannel) key.channel());
        Packet packet = packageReceived;
        //if(packageReceived.getMethod().equals("info"))
            packet = new Packet("ianime", null, null);
        ByteBuffer buffer = ByteBuffer.wrap(packet.serialize());
        channel.write(buffer);
        channel.register(key.selector(), SelectionKey.OP_READ);
    }
//    private void connect(SelectionKey key) throws IOException {
//        SocketChannel channel = ((SocketChannel) key.channel());
//        Attachment attachment = ((Attachment) key.attachment());
//        channel.finishConnect();
//
//        attachment.inBuffer = ByteBuffer.allocate(bufferSize);
//        attachment.inBuffer.put(OK).flip();
//        attachment.outBuffer = ((Attachment) attachment.peer.attachment()).inBuffer;
//        ((Attachment) attachment.peer.attachment()).outBuffer = attachment.inBuffer;
//        attachment.peer.interestOps(SelectionKey.OP_WRITE | SelectionKey.OP_READ);
//        key.interestOps(0);
//    }
//    private void close(SelectionKey key) throws IOException {
//        key.cancel();
//        key.channel().close();
////        key.cancel();
////        key.channel().close();
////        SelectionKey peerKey = ((Attachment) key.attachment()).peer;
////        if (peerKey != null) {
////            ((Attachment) peerKey.attachment()).peer = null;
////            if ((peerKey.interestOps() & SelectionKey.OP_WRITE) == 0) {
////                ((Attachment) peerKey.attachment()).outBuffer.flip();
////            }
////            peerKey.interestOps(SelectionKey.OP_WRITE);
////        }
//    }
}

