package MethodsClasses;

import Other.*;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZonedDateTime;
import java.util.*;

public abstract class Methods {
    public static LinkedHashMap<Integer, MusicBand> musicBands = new LinkedHashMap<>();
    private static final ZonedDateTime date = ZonedDateTime.now();

    /**
     *Выводит справку по доступным командам
     */
    public static void help(){
        System.out.println(
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
                        "print_field_descending_label : вывести значения поля label всех элементов в порядке убывания");
    }
    /**
     *Выводит в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)
     */
    public static void info(){
        System.out.println(
                "Тип: " + MusicBand.class
                        + "\nДата инициализации: " + date
                        + "\nКоличество элементов: " + musicBands.size()
        );
    }
    /**
     *Выводит в стандартный поток вывода все элементы коллекции в строковом представлении
     */
    public static void show(){
        if (musicBands.size()>0)
            musicBands.forEach((key, value) -> System.out.println(value));
        else
            System.out.println("Коллекция пуста");
    }
    /**
     *Добавляет новый элемент с заданным ключом
     */
    public static boolean insert(MusicBand musicBand, int key){
        if(musicBands.putIfAbsent(key, musicBand) == null) {
            System.out.println("Элемент добавлен");
            return true;

        }
        else {
            System.out.println("Такой элемент уже существует");
            return false;
        }
    }
    /**
     *Обновляет значение элемента коллекции, id которого равен заданному
     */
    public static boolean update(MusicBand musicBand, int id) {
        for (Map.Entry<Integer, MusicBand> entry : musicBands.entrySet()) {
            MusicBand value = entry.getValue();
            if(value.getId() == id) {
                musicBands.replace(entry.getKey(), value, musicBand);
                System.out.println("Элемент обновлен");
                return true;
            }
        }
        System.out.println("Нет элемента с таким id");
        return false;

    }
    /**
     *Удаляет элемент из коллекции по его ключу
     */
    public static boolean remove_key(int key) {
        if(musicBands.remove(key) == null) {
            System.out.println("Нет элемента с таким key");
            return false;
        }
        else {
            System.out.println("Элемент удален");
            return true;
        }

    }
    /**
     *Очищает коллекцию
     */
    public static void clear(){
        if(musicBands.isEmpty())
            System.out.println("Коллекция и так пуста");
        else {
            musicBands.clear();
            System.out.println("Коллекция очищена");
        }
    }
    /**
     *Сохраняет коллекцию в файл CSV
     */
    public static void save(Connection connection) throws IOException {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM BANDS");
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
                                "admin" + "')");
            }
            statement.close();
            System.out.println("Коллекция сохранена");
        }
        catch (SQLException e) {
            System.out.println("Произошла ошибка: " + e.getMessage());;
        }
    }
    /**
     *Удаляет из коллекции все элементы, превышающие заданный numberOfParticipants
     */
    public static boolean remove_greater(MusicBand musicBand){
        LinkedHashMap<Integer, MusicBand> musicBandsNew = (LinkedHashMap<Integer, MusicBand>) musicBands.clone();
        int number = musicBand.getNumberOfParticipants();
        musicBands.forEach((key, value) ->{
            if(value.getNumberOfParticipants() > number)
                musicBandsNew.remove(key);
        });
        if(musicBandsNew.size() == musicBands.size()) {
            System.out.println("Нет таких элементов");
            return false;
        }
        else {
            musicBands = musicBandsNew;
            System.out.println("Элементы удалены");
            return true;
        }
    }
    /**
     *Удаляет из коллекции все элементы, меньшие, чем заданный numberOfParticipants
     */
    public static boolean remove_lower(MusicBand musicBand){
        LinkedHashMap<Integer, MusicBand> musicBandsNew = (LinkedHashMap<Integer, MusicBand>) musicBands.clone();
        int number = musicBand.getNumberOfParticipants();
        musicBands.forEach((key, value) ->{
            if(value.getNumberOfParticipants() < number)
                musicBandsNew.remove(key);
        });
        if(musicBandsNew.size() == musicBands.size()) {
            System.out.println("Нет таких элементов");
            return false;
        }
        else {
            musicBands = musicBandsNew;
            System.out.println("Элементы удалены");
            return true;
        }
    }
    /**
     *Удаляет из коллекции все элементы, превышающие заданный numberOfParticipants
     */
    public static boolean remove_greater_key(int keyToDelete){
        LinkedHashMap<Integer, MusicBand> musicBandsNew = (LinkedHashMap<Integer, MusicBand>) musicBands.clone();
        musicBands.forEach((key, value) ->{
            if(key > keyToDelete)
                musicBandsNew.remove(key);
        });
        if(musicBandsNew.size() == musicBands.size()) {
            System.out.println("Нет таких элементов");
            return false;
        }
        else {
            musicBands = musicBandsNew;
            System.out.println("Элементы удалены");
            return true;
        }
    }
    /**
     *Удаляет из коллекции один элемент, значение поля establishmentDate которого эквивалентно заданному
     */
    public static boolean remove_any_by_establishment_date(ZonedDateTime establishmentDate) {
        for (Map.Entry<Integer, MusicBand> entry : musicBands.entrySet()) {
            MusicBand value = entry.getValue();
            if(value.getEstablishmentDate().equals(establishmentDate)) {
                System.out.println("Элемент удален");
                musicBands.remove(entry.getKey(), value);
                return true;
            }
        }
        System.out.println("Нет таких элементов");
        return false;
    }
    /**
     *Выводит уникальные значения поля genre всех элементов в коллекции
     */
    public static void print_unique_genre() {
        if(!musicBands.isEmpty()) {
            LinkedList<MusicGenre> uniqueGenres = new LinkedList<>();
            musicBands.forEach((key, value) -> {
                if (!uniqueGenres.contains(value.getGenre()))
                    uniqueGenres.add(value.getGenre());
            });
            System.out.println("Уникальные значения в коллекции:");
            for (MusicGenre genre : uniqueGenres)
                System.out.println(genre);
        }
        else
            System.out.println("Коллекция пуста");
    }
    /**
     *Выводит значения поля label всех элементов в порядке убывания
     */
    public static void print_field_descending_label() {
        if(!musicBands.isEmpty()) {
            TreeSet<Label> labels = new TreeSet<>();
            musicBands.forEach((key, value) -> labels.add(value.getLabel()));
            System.out.println("Значения label:");
            for (Label label : labels)
                System.out.println(label);
        }
        else
            System.out.println("Коллекция пуста");
    }
    /**
     *Загружает коллекцию из CSV файла
     */
    public static void upload(Connection connection){
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
            System.out.println("Коллекция загружена");
        }
        catch (SQLException e) {
            System.out.println("Произошла ошибка: " + e.getMessage());
        }
    }
    /**
     *Считывает следующее слово с консоли/терминала
     */
    public static String readWord(){
        try {
            System.out.print(">>>");
            Scanner sc = new Scanner(System.in);
            return sc.next();
        } catch (Exception e) {
            return null;
        }
    }
    /**
     *Считывает строку с консоли/терминала
     */
    public static String readLine(){
        try {
            System.out.print(">>>");
            Scanner sc = new Scanner(System.in);
            return sc.nextLine();
        } catch (Exception e) {
            return null;
        }
    }
    /**
     *Выводит информацию о получении ошибки и спрашивает повторный ввод
     */
    public static boolean repeatInput(){
        boolean flag = true;
        System.out.println("Повторить ввод?\nДа - любое значение\nНет - no");
        if (Methods.readWord().equals("no"))
            flag = false;
        return flag;
    }
}
