package Mains;

import MethodsClasses.Methods;
import Other.*;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;

public class MainClient {
    static SocketChannel client;
    static String login = null;
    static String pass = null;
    public static void main(String[] args) {
        boolean flag = true;
        try {
            client = SocketChannel.open();
            InetSocketAddress inetSocketAddress = new InetSocketAddress("localhost", 8888);
            if (!client.connect(inetSocketAddress)) {
                while (!client.finishConnect()) {
                    System.out.println("Ошибка подключения");
                }
            }
            while (flag) {
                System.out.println("Введите команду\nПодсказка: \"help\" для получения информации о командах");
                String method = Methods.readWord();

                try {
                    flag = checkMethod(method);
                } catch (NullPointerException e) {
                    System.out.println("Конец файла. Завершение работы...");
                    System.exit(0);
                } catch (ClassNotFoundException e) {
                    System.out.println("Классы не найдены. Дальнейшая работа бессмысленна");
                    flag = false;
                } catch (NumberFormatException e) {
                    System.out.println("Невертный формат данных");
                }
            }
        } catch (IOException e) {
            System.out.println("Подключение сброшено");
        } catch (Exception e) {
            System.out.println("Завершение работы");
        }
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
                            login
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
    private static String [] tryToLogin() {
        String[] parameters = new String[2];
        System.out.println("Введите Логин:");
        parameters[0] = Methods.readWord();
        System.out.println("Введите Пароль:");
        parameters[1] = Methods.readWord();
        return parameters;
    }
    public static boolean checkMethod(String method) throws NullPointerException, IOException, ClassNotFoundException {
        boolean flag = true;
        switch (method){
            case "enter":{
                while (flag) {
                String [] parameters = tryToLogin();
                write(new Packet("enter", null, null, parameters[0], parameters[1]));
                    if (read().getParameter().equals("1")) {
                        System.out.println("Вы вошли");
                        login = parameters[0];
                        pass = parameters[1];
                        write(new Packet("upload", null, null, login, pass));
                        System.out.println(read().getParameter());
                        flag = false;
                    } else {
                        System.out.println("Неправильное имя или пароль");
                        flag = Methods.repeatInput();
                    }
                }
                flag = true;
                break;
            }
            case "reg":{
                while (flag) {
                    String[] parameters = tryToLogin();
                    write(new Packet("reg", null, null, parameters[0], parameters[1]));
                    if (read().getParameter().equals("1")) {
                        System.out.println("Вы зарегестрировались");
                        login = parameters[0];
                        pass = parameters[1];
                        write(new Packet("upload", null, null, login, pass));
                        System.out.println(read().getParameter());
                    } else {
                        System.out.println("Такой пользователь уже существует");
                        flag = Methods.repeatInput();
                    }
                    flag = false;
                }
                flag = true;
                break;
            }
            case "help":{
                Methods.help();
//                write(new Packet("help", null, null, login, pass));
//                System.out.println(read().getParameter());
                break;
            }
            case "info":{
                write(new Packet("info", null, null, login, pass));
                System.out.println(read().getParameter());
                break;
            }
            case "show":{
                write(new Packet("show", null, null, login, pass));
                System.out.println(read().getParameter());
                break;
            }
            case "insert":{
                MusicBand musicBand = getBand();
                while (flag) {
                    if(musicBand != null) {
                        System.out.println("Введите key группы:");
                        try {
                            int key = Integer.parseInt(Methods.readWord());
                            write(new Packet("insert", String.valueOf(key), musicBand, login, pass));
                            System.out.println(read().getParameter());
                            flag = false;
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
                            write(new Packet("update", String.valueOf(key), musicBand, login, pass));
                            System.out.println(read().getParameter());
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
                        write(new Packet("remove_key", String.valueOf(key), null, login, pass));
                        System.out.println(read().getParameter());
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
                write(new Packet("clear", null, null, login, pass));
                System.out.println(read().getParameter());
                break;
            }
            case "save":{
                write(new Packet("save", null, null, login, pass));
                System.out.println(read().getParameter());
                break;
            }
            case "exit":{
                System.exit(0);
                System.out.println("Вы вышли");
                break;
            }
            case "remove_greater": {
                MusicBand musicBand = getBand();
                if(musicBand != null) {
                    write(new Packet("remove_greater", null, musicBand, login, pass));
                    System.out.println(read().getParameter());
                }
                break;
            }
            case "remove_lower": {
                MusicBand musicBand = getBand();
                if(musicBand != null) {
                    write(new Packet("remove_lower", null, musicBand, login, pass));
                    System.out.println(read().getParameter());
                }
                break;
            }
            case "remove_greater_key": {
                while (flag) {
                    System.out.println("Введите key группы:");
                    try {
                        int key = Integer.parseInt(Methods.readWord());
                        write(new Packet("remove_greater_key", String.valueOf(key), null, login, pass));
                        System.out.println(read().getParameter());
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
                        write(new Packet("remove_any_by_establishment_date", date.toString(), null, login, pass));
                        System.out.println(read().getParameter());
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
                write(new Packet("print_unique_genre", null, null, login, pass));
                System.out.println(read().getParameter());
                break;
            }
            case "print_field_descending_label": {
                write(new Packet("print_field_descending_label", null, null, login, pass));
                System.out.println(read().getParameter());
                break;
            }
            case "upload": {
                write(new Packet("upload", null, null, login, pass));
                System.out.println(read().getParameter());
                break;
            }
            default:{
                System.out.println("Неверная команда. Попробуйте еще раз");
            }
        }
        return flag;
    }
    public static void write(Packet packet) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(packet.serialize());
        client.write(buffer);
    }
    public static Packet read() throws IOException, ClassNotFoundException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        client.read(buffer);
        return Packet.deserialize(buffer.array());
    }
}
