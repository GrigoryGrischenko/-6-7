package Other;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class MusicBand implements Serializable {
    private static int idAll = 1;
    private int id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private java.time.ZonedDateTime creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Integer numberOfParticipants; //Поле может быть null, Значение поля должно быть больше 0
    private java.time.ZonedDateTime establishmentDate; //Поле может быть null
    private MusicGenre genre; //Поле может быть null
    private Label label; //Поле не может быть null
    private String author;

    public MusicBand(String name, Coordinates coordinates, Integer numberOfParticipants, ZonedDateTime establishmentDate, MusicGenre genre, Label label, String author) {
        creationDate = ZonedDateTime.now();
        id = idAll;
        idAll++;

        this.name = name;
        this.coordinates = coordinates;
        this.numberOfParticipants = numberOfParticipants;
        this.establishmentDate = establishmentDate;
        this.genre = genre;
        this.label = label;
        this.author = author;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public ZonedDateTime getCreationDate() {
        return creationDate;
    }

    public Integer getNumberOfParticipants() {
        return numberOfParticipants;
    }

    public ZonedDateTime getEstablishmentDate() {
        return establishmentDate;
    }

    public MusicGenre getGenre() {
        return genre;
    }

    public Label getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return "Other.MusicBand{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", x=" + coordinates.getX() +
                ", y=" + coordinates.getY() +
                ", creationDate=" + creationDate +
                ", numberOfParticipants=" + numberOfParticipants +
                ", establishmentDate=" + establishmentDate +
                ", genre=" + genre +
                ", label=" + label.getName() +
                ", labelBands=" + label.getBands() +
                ", labelSales=" + label.getSales() +
                ", author=" + author +
                '}';
    }

    public String getAuthor() {
        return author;
    }
}
