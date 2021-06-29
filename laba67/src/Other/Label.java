package Other;

import java.io.Serializable;

public class Label implements Comparable, Serializable {
    private String name;
    private Long bands; //Поле может быть null
    private Integer sales; //Поле не может быть null, Значение поля должно быть больше 0

    public Label (String name, Long bands, Integer sales) {
        this.name = name;
        this.bands = bands;
        this.sales = sales;
    }

    public String getName() {
        return name;
    }

    public Long getBands() {
        return bands;
    }

    public Integer getSales() {
        return sales;
    }

    @Override
    public String toString() {
        return "Other.Label{" +
                "name='" + name + '\'' +
                ", bands=" + bands +
                ", sales=" + sales +
                '}';
    }

    @Override
    public int compareTo(Object o) {
        return ((Label)o).sales - sales;
    }
}
