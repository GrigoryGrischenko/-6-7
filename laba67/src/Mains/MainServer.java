package Mains;

import Threads.Server;
import Threads.ServerThreadPool;
import org.sqlite.JDBC;

import java.math.BigInteger;
import java.net.PortUnreachableException;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.time.ZonedDateTime;

public class MainServer {
    static ServerThreadPool server;
    final static String HOST = "localhost";
    final static int PORT = 8888;
    final static String PASS = "1234";
    final static String USER = "user";
    final static String DB_URL = "jdbc:sqlite:C:\\Users\\DNS\\Desktop\\Server.db";
    public static void main(String[] args) {
        try {
            DriverManager.registerDriver(new JDBC());
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement statement = connection.createStatement();

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest("1234".getBytes());
            String password = String.format("%064x", new BigInteger(1, hash));

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS USERS (user PRIMARY KEY, password)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS BANDS (id INTEGER PRIMARY KEY AUTOINCREMENT, name, x DOUBLE, y FLOAT, creationDate, number INTEGER, establishmentDate, genre, labelName, labelBands INTEGER, labelSales INTEGER, author)");
            //statement.executeUpdate("INSERT INTO USERS ('user', 'password') VALUES ('user', '" + password + "')");
            //statement.executeUpdate("INSERT INTO BANDS (name, x, y, creationDate, number, establishmentDate, genre, labelName, labelBands, labelSales, author) VALUES ('Moneskin', 1, 22, '"+ ZonedDateTime.now() + "', 5, '" + ZonedDateTime.now() + "', 'ROCK', 'none', 1, 1000, 'user')");
            //C:\Users\creog\Desktop\Developing\Java\Trushin\Commands.txt
            statement.close();
            new Server(connection);
            server = new ServerThreadPool(connection, HOST, PORT);

        }
        catch (Exception e) {
            System.out.println("Сервер остановлен из-за непредвиденной ошибки: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
