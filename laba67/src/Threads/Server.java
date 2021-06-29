package Threads;

import MethodsClasses.Methods;
import Other.Coordinates;
import Other.Label;
import Other.MusicBand;
import Other.MusicGenre;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class Server extends Thread{
    Connection connection;
    public Server (Connection connection) {
        this.connection = connection;
        start();
    }

    @Override
    public void run() {
        Methods.upload(connection);
        boolean flag = true;
        while (flag) {
            System.out.println("Введите команду\nПодсказка: \"help\" для получения информации о командах");
            String method = Methods.readWord();

            try {
                flag = checkMethod(method, connection);
            } catch (NullPointerException e) {
                System.out.println("Конец файла. Завершение работы...");
                System.exit(0);
            }
        }
    }
    public static boolean checkMethod(String method, Connection connection) throws NullPointerException {
        boolean flag = true;
        switch (method){
            case "help":{
                Methods.help();
                break;
            }
            case "info":{
                Methods.info();
                break;
            }
            case "show":{
                Methods.show();
                break;
            }
            case "insert":{
                MusicBand musicBand = getBand();
                while (flag) {
                    if(musicBand != null) {
                        System.out.println("Введите key группы:");
                        try {
                            int key = Integer.parseInt(Methods.readWord());
                            Methods.insert(musicBand, key);
                            break;
                        } catch (NullPointerException e) {
                            System.out.println("Введено неверное значение key");
                            flag = Methods.repeatInput();
                        }
                    }
                    else
                        break;
                }
                flag = true;
                break;
            }
            case "update": {
                MusicBand musicBand = getBand();
                while (flag) {
                    if(musicBand != null) {
                        System.out.println("Введите key группы:");
                        try {
                            int key = Integer.parseInt(Methods.readWord());
                            Methods.update(musicBand, key);
                            break;
                        } catch (NullPointerException e) {
                            System.out.println("Введено неверное значение key");
                            flag = Methods.repeatInput();
                        }
                    }
                    else
                        break;
                }
                flag = true;
                break;
            }
            case "remove_key":{
                while (flag) {
                    System.out.println("Введите key группы:");
                    try {
                        int key = Integer.parseInt(Methods.readWord());
                        Methods.remove_key(key);
                        break;
                    } catch (NullPointerException e) {
                        System.out.println("Введено неверное значение key");
                        flag = Methods.repeatInput();
                    }
                }
                flag = true;
                break;
            }
            case "clear":{
                Methods.clear();
                break;
            }
            case "save":{
                try {
                    Methods.save(connection);
                } catch (IOException e) {
                    System.out.println("Возникла ошибка. Параметры ошибки:");
                    e.printStackTrace();
                }
                break;
            }
            case "exit":{
                System.exit(0);
                System.out.println("Вы вышли");
                break;
            }
            case "execute_script": {
                while (flag) {
                    System.out.println("Введите путь к файлу, включая его имя:");
                    File fileWithMethods;
                    try{
                        fileWithMethods = new File(Methods.readLine());
                        try {
                            Scanner sc = new Scanner(fileWithMethods);
                            while (sc.hasNext()){
                                String methodFromFile = sc.nextLine();
                                String[] parameters = methodFromFile.split(" ");

                                String argument = "";
                                for(int i = 1; i < parameters.length; i++) {
                                    argument += parameters[i];
                                }
                                switch (parameters[0]) {
                                    case "help": {
                                        System.out.println(">>>Из файла>>>help:");
                                        Methods.help();
                                        break;
                                    }
                                    case "info": {
                                        System.out.println(">>>Из файла>>>info:");
                                        Methods.info();
                                        break;
                                    }
                                    case "show": {
                                        System.out.println(">>>Из файла>>>show:");
                                        Methods.show();
                                        break;
                                    }
                                    case "insert": {
                                        System.out.println(">>>Из файла>>>insert:");
                                        parameters = argument.split(";");
                                        try {
                                            Methods.insert(new MusicBand(
                                                    parameters[0],
                                                    new Coordinates(Double.parseDouble(parameters[1]), Float.parseFloat(parameters[2])),
                                                    Integer.parseInt(parameters[3]),
                                                    ZonedDateTime.parse(parameters[4]),
                                                    MusicGenre.valueOf(parameters[5]),
                                                    new Label(parameters[6], Long.parseLong(parameters[7]), Integer.parseInt(parameters[8])),
                                                    "admin"
                                            ), Integer.parseInt(parameters[9]));
                                        } catch (ArrayIndexOutOfBoundsException e) {
                                            System.out.println("Не достает данных: " + e.getMessage());
                                        }
                                        catch (Exception e) {
                                            System.out.println("Неверный формат данных" + e.getMessage());
                                        }
                                        break;
                                    }
                                    case "update": {
                                        System.out.println(">>>Из файла>>>update:");
                                        parameters = argument.split(";");
                                        try {
                                            Methods.update(new MusicBand(
                                                    parameters[0],
                                                    new Coordinates(Double.parseDouble(parameters[1]), Float.parseFloat(parameters[2])),
                                                    Integer.parseInt(parameters[3]),
                                                    ZonedDateTime.parse(parameters[4]),
                                                    MusicGenre.valueOf(parameters[5]),
                                                    new Label(parameters[6], Long.parseLong(parameters[7]), Integer.parseInt(parameters[8])),
                                                    parameters[10]
                                            ), Integer.parseInt(parameters[9]));
                                        }
                                        catch (Exception e) {
                                            System.out.println("Неверный формат данных");
                                        }
                                        break;
                                    }
                                    case "remove_key": {
                                        System.out.println(">>>Из файла>>>remove_key:");
                                        try {
                                            int key = Integer.parseInt(argument);
                                            Methods.remove_key(key);
                                        }
                                        catch (Exception e) {
                                            System.out.println("Неверный формат данных");
                                        }
                                        break;
                                    }
                                    case "clear": {
                                        System.out.println(">>>Из файла>>>clear:");
                                        Methods.clear();
                                        break;
                                    }
                                    case "save": {
                                        System.out.println(">>>Из файла>>>save:");
                                        try {
                                            Methods.save(connection);
                                        } catch (IOException e) {
                                            System.out.println("Возникла ошибка. Параметры ошибки:");
                                            e.printStackTrace();
                                        }
                                        break;
                                    }
                                    case "exit": {
                                        System.out.println(">>>Из файла>>>exit:");
                                        System.out.println("Вы вышли");
                                        System.exit(0);
                                        break;
                                    }
                                    case "remove_greater": {
                                        System.out.println(">>>Из файла>>>remove_greater:");
                                        parameters = argument.split(";");
                                        try {
                                            Methods.remove_greater(new MusicBand(
                                                    parameters[0],
                                                    new Coordinates(Double.parseDouble(parameters[1]), Float.parseFloat(parameters[2])),
                                                    Integer.parseInt(parameters[3]),
                                                    ZonedDateTime.parse(parameters[4]),
                                                    MusicGenre.valueOf(parameters[5]),
                                                    new Label(parameters[6], Long.parseLong(parameters[7]), Integer.parseInt(parameters[8])),
                                                    "admin"
                                            ));
                                        }
                                        catch (Exception e) {
                                            System.out.println("Неверный формат данных");
                                        }
                                        break;
                                    }
                                    case "remove_lower": {
                                        System.out.println(">>>Из файла>>>remove_lower:");
                                        parameters = argument.split(";");
                                        try {
                                            Methods.remove_lower(new MusicBand(
                                                    parameters[0],
                                                    new Coordinates(Double.parseDouble(parameters[1]), Float.parseFloat(parameters[2])),
                                                    Integer.parseInt(parameters[3]),
                                                    ZonedDateTime.parse(parameters[4]),
                                                    MusicGenre.valueOf(parameters[5]),
                                                    new Label(parameters[6], Long.parseLong(parameters[7]), Integer.parseInt(parameters[8])),
                                                    "admin"
                                            ));
                                        }
                                        catch (Exception e) {
                                            System.out.println("Неверный формат данных");
                                        }
                                        break;
                                    }
                                    case "remove_greater_key": {
                                        System.out.println(">>>Из файла>>>remove_greater_key:");
                                        try {
                                            int key = Integer.parseInt(argument);
                                            Methods.remove_greater_key(key);
                                        }
                                        catch (Exception e) {
                                            System.out.println("Неверный формат данных");
                                        }
                                        break;
                                    }
                                    case "remove_any_by_establishment_date": {
                                        System.out.println(">>>Из файла>>>remove_any_by_establishment_date:");
                                        try {
                                            ZonedDateTime date = ZonedDateTime.parse(argument + "+03:00");
                                            Methods.remove_any_by_establishment_date(date);
                                        }
                                        catch (Exception e) {
                                            System.out.println("Неверный формат данных");
                                        }
                                        break;
                                    }
                                    case "print_unique_genre": {
                                        System.out.println(">>>Из файла>>>print_unique_genre:");
                                        Methods.print_unique_genre();
                                        break;
                                    }
                                    case "print_field_descending_label": {
                                        System.out.println(">>>Из файла>>>print_field_descending_label:");
                                        Methods.print_field_descending_label();
                                        break;
                                    }
                                    default: {
                                        System.out.println("Неверная команда в файле");
                                    }
                                }
                            }
                            flag = false;
                        }
                        catch (FileNotFoundException e1) {
                            System.out.println("Файл не найден");
                        }
                    }
                    catch (NullPointerException e ){
                        System.out.println("Некорректный ввод");
                        flag = Methods.repeatInput();
                    }
                }
                flag = true;
                break;
            }
            case "remove_greater": {
                MusicBand musicBand = getBand();
                if(musicBand != null)
                    Methods.remove_greater(musicBand);
                break;
            }
            case "remove_lower": {
                MusicBand musicBand = getBand();
                if(musicBand != null)
                    Methods.remove_lower(musicBand);
                break;
            }
            case "remove_greater_key": {
                while (flag) {
                    System.out.println("Введите key группы:");
                    try {
                        int key = Integer.parseInt(Methods.readWord());
                        Methods.remove_greater_key(key);
                        break;
                    } catch (NullPointerException e) {
                        System.out.println("Введено неверное значение key");
                        flag = Methods.repeatInput();
                    }
                }
                flag = true;
                break;
            }
            case "remove_any_by_establishment_date": {
                while (flag) {
                    System.out.println("Введите establishmentDate группы:\nПодсказка: формат даты выглядит как: \"YYYY-MM-DDTHH:MM:SS\"\nПример: 2020-10-10T13:13:13");
                    try {
                        ZonedDateTime date = ZonedDateTime.parse(Methods.readLine() + "+03:00");
                        Methods.remove_any_by_establishment_date(date);
                        break;
                    } catch (DateTimeParseException e) {
                        System.out.println("Введено неверное значение ZonedDateTime");
                        flag = Methods.repeatInput();
                    }
                }
                flag = true;
                break;
            }
            case "print_unique_genre": {
                Methods.print_unique_genre();
                break;
            }
            case "print_field_descending_label": {
                Methods.print_field_descending_label();
                break;
            }
            case "upload": {
                Methods.upload(connection);
                break;
            }
            default:{
                System.out.println("Неверная команда. Попробуйте еще раз");
            }
        }
        return flag;
    }
    private static MusicBand getBand() {
        boolean flag = true;
        while (flag) {
            try {
                String[] parameters = new String[9];
                System.out.println("Введите Название группы:");
                parameters[0] = Methods.readWord();
                System.out.println("Введите Координату Х группы:");
                parameters[1] = String.valueOf(Double.parseDouble(Methods.readWord()));
                System.out.println("Введите Координату Y группы:");
                parameters[2] = String.valueOf(Float.parseFloat(Methods.readWord()));
                System.out.println("Введите Количество участников группы:");
                parameters[3] = String.valueOf(Integer.parseInt(Methods.readWord()));
                System.out.println("Введите Дату создания группы:\nПодсказка: формат даты выглядит как: \"YYYY-MM-DDTHH:MM:SS\"\nПример: 2020-10-10T13:13:13");
                parameters[4] = ZonedDateTime.parse(Methods.readWord() + "+03:00").toString();
                System.out.println("Введите Музыкальный жанр:\nДоступные жанры: \"ROCK\", \"HIP_HOP\". \"SOUL\", \"PUNK_ROCK\"");
                parameters[5] = MusicGenre.valueOf(Methods.readWord()).name();
                System.out.println("Введите Имя лейбла:");
                parameters[6] = Methods.readWord();
                System.out.println("Введите Количество Групп лейбла:");
                parameters[7] = String.valueOf(Long.parseLong(Methods.readWord()));
                System.out.println("Введите Количетсво Продаж лейбла:");
                parameters[8] = String.valueOf(Integer.parseInt(Methods.readWord()));

                try {
                    return new MusicBand(
                            parameters[0],
                            new Coordinates(Double.parseDouble(parameters[1]), Float.parseFloat(parameters[2])),
                            Integer.parseInt(parameters[3]),
                            ZonedDateTime.parse(parameters[4]),
                            MusicGenre.valueOf(parameters[5]),
                            new Label(parameters[6], Long.parseLong(parameters[7]), Integer.parseInt(parameters[8])),
                            "admin"
                    );
                } catch (Exception e) {
                    System.out.println("Какие-то параметры почему-то неверны" + e.getMessage());
                    flag = Methods.repeatInput();
                }

            } catch (NumberFormatException e) {
                System.out.println("Введено неверное значение числа");
                flag = Methods.repeatInput();
            } catch (DateTimeParseException e) {
                System.out.println("Введено неверное значение даты");
                flag = Methods.repeatInput();
            } catch (IllegalArgumentException e) {
                System.out.println("Введено неверное значение жанра");
                flag = Methods.repeatInput();
            }
        }
        return null;
    }
}
