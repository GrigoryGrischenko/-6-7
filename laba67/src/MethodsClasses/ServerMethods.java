package MethodsClasses;

import Other.*;
import org.sqlite.SQLiteException;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.*;

public abstract class ServerMethods {
    public static LinkedHashMap<Integer, MusicBand> musicBands = new LinkedHashMap<>();
    private static final ZonedDateTime date = ZonedDateTime.now();

    /**
     *Выводит справку по доступным командам
     */
    public static Packet help(){
        return new Packet( null,
                "help : вывести справку по доступным командам\n" +
                        "info : вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)\n" +
                        "show : вывести в стандартный поток вывода все элементы коллекции в строковом представлении\n" +
                        "insert null {element} : добавить новый элемент с заданным ключом\n" +
                        "update id {element} : обновить значение элемента коллекции, id которого равен заданному\n" +
                        "remove_key null : удалить элемент из коллекции по его ключу\n" +
                        "clear : очистить коллекцию\n" +
                        "save : сохранить коллекцию в файл\n" +
                        "execute_script file_name : считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.\n" +
                        "exit : завершить программу (без сохранения в файл)\n" +
                        "remove_greater {element} : удалить из коллекции все элементы, превышающие заданный\n" +
                        "remove_lower {element} : удалить из коллекции все элементы, меньшие, чем заданный\n" +
                        "remove_greater_key null : удалить из коллекции все элементы, ключ которых превышает заданный\n" +
                        "remove_any_by_establishment_date establishmentDate : удалить из коллекции один элемент, значение поля establishmentDate которого эквивалентно заданному\n" +
                        "print_unique_genre : вывести уникальные значения поля genre всех элементов в коллекции\n" +
                        "print_field_descending_label : вывести значения поля label всех элементов в порядке убывания", null
        );
    }
    /**
     *Выводит в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)
     */
    public static Packet info(){
        return new Packet( null,
                "Тип: " + MusicBand.class
                        + "\nДата инициализации: " + date
                        + "\nКоличество элементов: " + musicBands.size(), null
        );
    }
    /**
     *Выводит в стандартный поток вывода все элементы коллекции в строковом представлении
     */
    public static Packet show(){
        String str = "";
        if (musicBands.size()>0)
            for (Map.Entry<Integer, MusicBand> entry : musicBands.entrySet())
                str += entry.getValue() + "\n";
        else
            str = "Коллекция пуста";
        return new Packet( null, str, null);
    }
    /**
     *Добавляет новый элемент с заданным ключом
     */
    public static Packet insert(Packet packet){
        if(musicBands.putIfAbsent(Integer.parseInt(packet.getParameter()), packet.getMusicBand()) == null)
            return new Packet( null, "Элемент добавлен", null);
        else
            return new Packet( null,"Такой элемент уже существует", null);
    }
    /**
     *Обновляет значение элемента коллекции, id которого равен заданному
     */
    public static Packet update(Packet packet) {
        for (Map.Entry<Integer, MusicBand> entry : musicBands.entrySet()) {
            MusicBand value = entry.getValue();
            if(value.getId() == Integer.parseInt(packet.getParameter()) ) {
                if(value.getAuthor().equals(packet.getUser())) {
                    musicBands.replace(entry.getKey(), value, packet.getMusicBand());
                    return new Packet(null, "Элемент обновлен", null);
                }
                else
                    return new Packet(null, "У вас недостаточно прав для выполнения этого действия", null);
            }
        }
        return new Packet( null,"Нет элемента с таким id", null);
    }
    /**
     *Удаляет элемент из коллекции по его ключу
     */
    public static Packet remove_key(Packet packet) {
        int key = Integer.parseInt(packet.getParameter());
        MusicBand musicBand = musicBands.get(key);

        if(musicBand == null)
            return new Packet( null,"Нет элемента с таким key", null);
        else {
            if(musicBand.getAuthor().equals(packet.getUser())) {
                musicBands.remove(key);
                return new Packet(null, "Элемент удален", null);
            }
            else
                return new Packet(null, "У вас недостаточно прав для выполнения этого действия", null);
        }
    }
    /**
     *Очищает коллекцию
     */
    public static Packet clear(Packet packet) {
        if (musicBands.isEmpty())
            return new Packet(null, "Коллекция и так пуста", null);
        else {
            LinkedHashMap<Integer, MusicBand> musicBandsNew = new LinkedHashMap<>();
            for (Map.Entry<Integer, MusicBand> entry : musicBands.entrySet()) {
                if (entry.getValue().getAuthor().equals(packet.getUser()))
                    musicBandsNew.remove(entry.getKey());

            }
            musicBands = musicBandsNew;
            return new Packet(null, "Коллекция очищена", null);
        }
    }
    /**
     *Сохраняет коллекцию в файл CSV
     */
    public static Packet save(Packet packet, Connection connection) {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM BANDS WHERE 'author' = '" + packet.getUser() + "'");
            for (Map.Entry<Integer, MusicBand> entry : musicBands.entrySet()) {
                MusicBand value = entry.getValue();
                statement.executeUpdate(
                        "INSERT INTO BANDS (name, x, y, creationDate, number, establishmentDate, genre, labelName, labelBands, labelSales, author) VALUES ('" +
                                value.getName() + "', " +
                                value.getCoordinates().getX() + ", " +
                                value.getCoordinates().getY()  + ", '" +
                                value.getCreationDate() + "', '" +
                                value.getNumberOfParticipants() + "', '" +
                                value.getCreationDate() + "', '" +
                                value.getGenre() + "', '" +
                                value.getLabel().getName() + "', '" +
                                value.getLabel().getBands() + "', '" +
                                value.getLabel().getSales() + "', '" +
                                packet.getUser() + "')");
            }
            statement.close();
            return new Packet(null, "Файл сохранен", null);
        }
        catch (SQLException e) {
            return new Packet(null, "Файл недоступен и не сохранен", null);
        }
    }
    /**
     *Удаляет из коллекции все элементы, превышающие заданный numberOfParticipants
     */
    public static Packet remove_greater(Packet packet){
        LinkedHashMap<Integer, MusicBand> musicBandsNew = (LinkedHashMap<Integer, MusicBand>) musicBands.clone();
        int number = packet.getMusicBand().getNumberOfParticipants();
        musicBands.forEach((key, value) ->{
            if(value.getNumberOfParticipants() > number && packet.getUser().equals(value.getAuthor()))
                musicBandsNew.remove(key);
        });
        if(musicBandsNew.size() == musicBands.size()) {
            return new Packet(null, "Нет таких элементов или у вас недостаточно прав", null);
        }
        else {
            musicBands = musicBandsNew;
            return new Packet(null, "Элементы удалены", null);
        }
    }
    /**
     *Удаляет из коллекции все элементы, меньшие, чем заданный numberOfParticipants
     */
    public static Packet remove_lower(Packet packet){
        LinkedHashMap<Integer, MusicBand> musicBandsNew = (LinkedHashMap<Integer, MusicBand>) musicBands.clone();
        int number = packet.getMusicBand().getNumberOfParticipants();
        musicBands.forEach((key, value) ->{
            if(value.getNumberOfParticipants() < number && packet.getUser().equals(value.getAuthor()))
                musicBandsNew.remove(key);
        });
        if(musicBandsNew.size() == musicBands.size()) {
            return new Packet(null, "Нет таких элементов или у вас недостаточно прав", null);
        }
        else {
            musicBands = musicBandsNew;
            return new Packet(null, "Элементы удалены", null);
        }
    }
    /**
     *Удаляет из коллекции все элементы, превышающие заданный numberOfParticipants
     */
    public static Packet remove_greater_key(Packet packet){
        LinkedHashMap<Integer, MusicBand> musicBandsNew = (LinkedHashMap<Integer, MusicBand>) musicBands.clone();
        int keyToDelete = Integer.parseInt(packet.getParameter());
        musicBands.forEach((key, value) ->{
            if(key > keyToDelete && packet.getUser().equals(value.getAuthor()))
                musicBandsNew.remove(key);
        });
        if(musicBandsNew.size() == musicBands.size()) {
            return new Packet(null, "Нет таких элементов", null);
        }
        else {
            musicBands = musicBandsNew;
            return new Packet(null, "Элементы удалены", null);
        }
    }
    /**
     *Удаляет из коллекции один элемент, значение поля establishmentDate которого эквивалентно заданному
     */
    public static Packet remove_any_by_establishment_date(Packet packet) {
        ZonedDateTime establishmentDate = ZonedDateTime.parse(packet.getParameter());
        for (Map.Entry<Integer, MusicBand> entry : musicBands.entrySet()) {
            MusicBand value = entry.getValue();
            if(value.getEstablishmentDate().equals(establishmentDate) && value.getAuthor().equals(packet.getUser())) {
                musicBands.remove(entry.getKey(), value);
                return new Packet(null, "Элемент удален", null);
            }
        }
        return new Packet(null, "Нет таких элементов", null);
    }
    /**
     *Выводит уникальные значения поля genre всех элементов в коллекции
     */
    public static Packet print_unique_genre() {
        String str = "";
        if(!musicBands.isEmpty()) {
            LinkedList<MusicGenre> uniqueGenres = new LinkedList<>();
            musicBands.forEach((key, value) -> {
                if (!uniqueGenres.contains(value.getGenre()))
                    uniqueGenres.add(value.getGenre());
            });
            str += "Уникальные значения в коллекции:";
            for (MusicGenre genre : uniqueGenres)
                str += genre;
        }
        else
            str = "Коллекция пуста";
        return new Packet(null, str, null);
    }
    /**
     *Выводит значения поля label всех элементов в порядке убывания
     */
    public static Packet print_field_descending_label() {
        String str = "";
        if(!musicBands.isEmpty()) {
            TreeSet<Label> labels = new TreeSet<>();
            musicBands.forEach((key, value) -> labels.add(value.getLabel()));
            str += "Значения label:";
            for (Label label : labels)
                str += label;
        }
        else
            str = "Коллекция пуста";
        return new Packet(null, str, null);
    }
    /**
     *Загружает коллекцию из CSV файла
     */
    public static Packet upload(Connection connection){
        try {
            LinkedHashMap<Integer, MusicBand> musicBandsNew = new LinkedHashMap<>();
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM BANDS");
            while(!resultSet.isAfterLast()){
                if(resultSet.getRow() != 0)
                    musicBandsNew.put(resultSet.getInt(1), new MusicBand(
                            resultSet.getString(2),
                            new Coordinates(resultSet.getDouble(3), resultSet.getFloat(4)),
                            resultSet.getInt(6),
                            ZonedDateTime.parse(resultSet.getString(7)),
                            MusicGenre.valueOf(resultSet.getString(8)),
                            new Label(resultSet.getString(9), resultSet.getLong(10), resultSet.getInt(11)),
                            resultSet.getString(12)
                    ));
                resultSet.next();
            }
            musicBands = musicBandsNew;
            return new Packet(null, "Файл загружен", null);
        }
        catch (SQLException e) {
            return new Packet(null, "Произошла ошибка: " + e.getMessage(), null);
        }
    }

    public static Packet reg(Packet packet, Connection connection) throws NoSuchAlgorithmException, SQLException {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(packet.getPass().getBytes());
            String password = String.format("%064x", new BigInteger(1, hash));
            connection.createStatement().executeUpdate("INSERT INTO USERS ('user', 'password') VALUES ('" + packet.getUser() + "', '" + password + "')");
            return new Packet(null, "1", null);
        } catch (SQLiteException e) {
            return new Packet(null, "0", null);
        }
    }
    public static Packet enter(Packet packet, Connection connection) throws NoSuchAlgorithmException, SQLException {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(packet.getPass().getBytes());
            String password = String.format("%064x", new BigInteger(1, hash));
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT password FROM USERS WHERE user ='" + packet.getUser() + "'");
            if (password.equals(resultSet.getString(1))) {
                return new Packet(null, "1", null);
            } else
                return new Packet(null, "0", null);
        } catch (SQLiteException e) {
            return new Packet(null, "0", null);
        }
    }
}
