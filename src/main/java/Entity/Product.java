package Entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name= "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String name;

    private long buyPrice;

    private long sellPrice;

    private Date buyDate;

    private Date sellDate;

    private boolean sold;

    private boolean bought;

    private long profit;

    public void calculateProfit(){profit=sellPrice-buyPrice;}
    public long getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(long sellPrice) {
        this.sellPrice = sellPrice;
    }

    public long getProfit() {
        return profit;
    }

    public void setProfit(long profit) {
        this.profit = profit;
    }

    public boolean isSold() {
        return sold;
    }

    public void setSold(boolean sold) {
        this.sold = sold;
    }

    public boolean isBought() {
        return bought;
    }

    public void setBought(boolean bought) {
        this.bought = bought;
    }

    public Date getBuyDate() {
        return buyDate;
    }

    public void setBuyDate(Date buyDate) {
        this.buyDate = buyDate;
    }

    public Date getSellDate() {
        return sellDate;
    }

    public void setSellDate(Date sellDate) {
        this.sellDate = sellDate;
    }

    public Product(String name) {
        this.name = name;
    }

    public Product() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(long price) {
        this.buyPrice = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Product(String name, long buyPrice, Date buyDate) {
        this.name = name;
        this.buyPrice = buyPrice;
        this.buyDate = buyDate;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", buyPrice=" + buyPrice +
                ", sellPrice=" + sellPrice +
                ", buyDate=" + buyDate +
                ", sellDate=" + sellDate +
                ", sold=" + sold +
                ", bought=" + bought +
                ", profit=" + profit +
                '}';
    }
}
