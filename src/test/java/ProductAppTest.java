import Entity.Product;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductAppTest {


    @Test
    void addProduct() {
        try {
            ProductApp.factory = new Configuration().configure().buildSessionFactory();//инициализируем SessionFactory для записи в БД
        } catch (Throwable ex) {
            System.err.println("Failed to create SessionFactory object");
            throw new ExceptionInInitializerError(ex);
        }
        ProductApp app=new ProductApp();
        String name="Картошечка";
        app.addProduct(new Product(name));
        Session session=ProductApp.factory.openSession(); //запускаем сессию
        Query query = session.createQuery("FROM Product where name=:paramName");
        query.setParameter("paramName", name);
        List<Product> products = query.list();
        boolean contains=false;
        if(products.size()!=0){
            contains=true;
        }
        session.close();
        Assertions.assertTrue(contains);
    }

    @Test
    void getProduct() {
        try {
            ProductApp.factory = new Configuration().configure().buildSessionFactory(); //инициализируем SessionFactory для записи в БД
        } catch (Throwable ex) {
            System.err.println("Failed to create SessionFactory object");
            throw new ExceptionInInitializerError(ex);
        }
        ProductApp app=new ProductApp();
        app.addProduct(new Product("Картофель")); //создаем товар в БД
        Date date = null;
        try{ date= DateFormat.format.parse("2020-02-01");}
                catch (ParseException e){e.printStackTrace();}
        app.getProduct("Картофель", 5, date,100);
        Session session=ProductApp.factory.openSession();  //запускаем сессию
        Query query = session.createQuery("FROM Product where name=:paramName and buyDate=:paramdate " +
                "and buyPrice=:parambuyprice");
        query.setParameter("paramName", "Картофель");
        query.setParameter("paramdate", date);
        query.setParameter("parambuyprice",100L);
        List<Product> products = query.list();
        Assertions.assertEquals(5, products.size());
    }

    @Test
    void sellProduct() {
        try {
            ProductApp.factory = new Configuration().configure().buildSessionFactory(); //инициализируем SessionFactory для записи в БД
        } catch (Throwable ex) {
            System.err.println("Failed to create SessionFactory object");
            throw new ExceptionInInitializerError(ex);
        }
        ProductApp app=new ProductApp();
        app.addProduct(new Product("Картофан")); //создаем товар в БД
        Date date = null;
        try{ date= DateFormat.format.parse("2020-02-01");}
        catch (ParseException e){e.printStackTrace();}
        app.getProduct("Картофан", 5, date,100);
        app.sellProduct("Картофан", 4, date, 200);
        Session session=ProductApp.factory.openSession();    //запускаем сессию
        Query query = session.createQuery("FROM Product where name=:paramName and sellDate=:paramdate " +
                "and sellPrice=:paramsellprice");
        query.setParameter("paramName", "Картофан");
        query.setParameter("paramdate", date);
        query.setParameter("paramsellprice",200L);
        List<Product> products = query.list();
        Assertions.assertEquals(4, products.size());
    }

    @Test
    void calculateTotalProfit() {
        try {
            ProductApp.factory = new Configuration().configure().buildSessionFactory(); //инициализируем SessionFactory для записи в БД
        } catch (Throwable ex) {
            System.err.println("Failed to create SessionFactory object");
            throw new ExceptionInInitializerError(ex);
        }
        ProductApp app=new ProductApp();
        app.addProduct(new Product("Бульба")); //создаем товар в БД
        Date date = null;
        Date date2= null;
        try{ date= DateFormat.format.parse("2020-02-01");
        date2=DateFormat.format.parse("2020-02-02");}
        catch (ParseException e){e.printStackTrace();}
        app.getProduct("Бульба", 5, date,100);
        app.sellProduct("Бульба", 4, date, 200);
        Assertions.assertEquals(400, app.calculateTotalProfit("Бульба",date2));
    }

    @Test()
    void getProductNegPrice() throws ParseException {
        try {
            ProductApp.factory = new Configuration().configure().buildSessionFactory(); //инициализируем SessionFactory для записи в БД
        } catch (Throwable ex) {
            System.err.println("Failed to create SessionFactory object");
            throw new ExceptionInInitializerError(ex);
        }
        ProductApp app=new ProductApp();
        app.addProduct(new Product("Картофель")); //создаем товар в БД
        final Date date= DateFormat.format.parse("2020-02-01");
        Assertions.assertThrows(IllegalArgumentException.class,()->{
            app.getProduct("Картофель", 5, date,-5);
        });

    }
}