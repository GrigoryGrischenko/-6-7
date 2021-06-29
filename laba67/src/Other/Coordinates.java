package Other;

import java.io.Serializable;

public class Coordinates implements Serializable {
    private Double x; //Поле не может быть null
    private Float y; //Максимальное значение поля: 968, Поле не может быть null

    public Coordinates(Double x, Float y) {
        this.x = x;
        this.y = y;
    }

    public Double getX() {
        return x;
    }

    public Float getY() {
        return y;
    }
}