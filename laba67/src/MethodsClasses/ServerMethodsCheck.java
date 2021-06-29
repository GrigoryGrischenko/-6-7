package MethodsClasses;

import Other.*;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;

public class ServerMethodsCheck {
    public Packet check(Packet packet, Connection connection) throws SQLException, NoSuchAlgorithmException {
        if(checkUser(packet, connection)) {
            switch (packet.getMethod()) {
                case "enter": {
                    return ServerMethods.enter(packet, connection);
                }
                case "reg": {
                    return ServerMethods.reg(packet, connection);
                }
                case "help": {
                    return ServerMethods.help();
                }
                case "info": {
                    return ServerMethods.info();
                }
                case "show": {
                    return ServerMethods.show();
                }
                case "insert": {
                    return ServerMethods.insert(packet);
                }
                case "update": {
                    return ServerMethods.update(packet);
                }
                case "remove_key": {
                    return ServerMethods.remove_key(packet);
                }
                case "clear": {
                    return ServerMethods.clear(packet);
                }
                case "save": {
                    return ServerMethods.save(packet, connection);
                }
                case "remove_greater": {
                    return ServerMethods.remove_greater(packet);
                }
                case "remove_lower": {
                    return ServerMethods.remove_lower(packet);
                }
                case "remove_greater_key": {
                    return ServerMethods.remove_greater_key(packet);
                }
                case "remove_any_by_establishment_date": {
                    return ServerMethods.remove_any_by_establishment_date(packet);
                }
                case "print_unique_genre": {
                    return ServerMethods.print_unique_genre();
                }
                case "print_field_descending_label": {
                    return ServerMethods.print_field_descending_label();
                }
                case "upload": {
                    return ServerMethods.upload(connection);
                }
                default: {
                    return new Packet(null, "Неверная команда", null);
                }
            }
        }
        else {
            if(packet.getMethod().equals("reg")) {
                return ServerMethods.reg(packet, connection);
            }
            else {
                return new Packet(null, "У вас нет прав для выполнения этого действия", null);
            }
        }
    }
    public boolean checkUser(Packet packet, Connection connection) throws NoSuchAlgorithmException {
        try {
            if (packet.getUser() == null)
                return false;
            String line = connection.createStatement().executeQuery("SELECT password FROM USERS WHERE user ='" + packet.getUser() + "'").getString(1);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(packet.getPass().getBytes());
            String password = String.format("%064x", new BigInteger(1, hash));

            return password.equals(line);
        } catch (SQLException e) {
            return false;
        }
    }
}
