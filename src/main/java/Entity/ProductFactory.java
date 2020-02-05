package Entity;


import javax.persistence.*;
import java.util.Date;

@Entity
public class ProductFactory extends Product{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private int quantity;

    private Date revenueDate;

    public Date getRevenueDate() {
        return revenueDate;
    }

    public void setRevenueDate(Date revenueDate) {
        this.revenueDate = revenueDate;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public ProductFactory() {
    }
}
