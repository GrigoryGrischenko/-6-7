package Other;

import java.io.*;

public class Packet implements Serializable {
    private String method;
    private String parameter;
    private MusicBand musicBand;
    private String user;
    private String pass;

    public Packet(String method, String parameter, MusicBand musicBand) {
        this.method = method;
        this.parameter = parameter;
        this.musicBand = musicBand;
    }
    public Packet(String method, String parameter, MusicBand musicBand, String user, String pass) {
        this(method, parameter, musicBand);
        this.user = user;
        this.pass = pass;
    }

    public byte[] serialize() throws IOException {
        try(ByteArrayOutputStream b = new ByteArrayOutputStream()){
            try(ObjectOutputStream o = new ObjectOutputStream(b)){
                o.writeObject(this);
            }
            return b.toByteArray();
        }
    }
    public static Packet deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        try(ByteArrayInputStream b = new ByteArrayInputStream(bytes)){
            try(ObjectInputStream o = new ObjectInputStream(b)){
                return (Packet) o.readObject();
            }
        }
    }

    public String getMethod() {
        return method;
    }

    public String getParameter() {
        return parameter;
    }

    public MusicBand getMusicBand() {
        return musicBand;
    }

    public String getUser() {
        return user;
    }

    public String getPass() {
        return pass;
    }
}
