import Entity.Product;
import Entity.ProductFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.util.Date;
import java.util.List;

import static spark.Spark.*;


public class ProductApp {
    public static SessionFactory factory;

    public static void main(String[] args) {
        try {
            factory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Failed to create SessionFactory object");
            throw new ExceptionInInitializerError(ex);
        }
        ObjectMapper mapper=new ObjectMapper();
        mapper.setDateFormat(DateFormat.format);
        port(7777);

        ProductApp app = new ProductApp();
        init();
        // POST- метод, в теле должна быть строка имени объекта для добавления в БД
        post("/newproduct", (request, response) ->{
            Product product=mapper.readValue(request.body(),Product.class);
            try{
                app.addProduct(product);
            }
            catch (IllegalArgumentException e){
                return "Такой товар уже есть в базе данных";
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return "Товар "+ product.getName() + " добавлен";
        }
        );
        // получение прибыли на указанную дату в формате строки (название /n дд-мм-гггг)
        get("/revenue",((request, response) -> {
            ProductFactory productFactory =mapper.readerFor(ProductFactory.class).readValue(request.body());
            Date date=productFactory.getRevenueDate();
            String name=productFactory.getName();
            return "Прибыль на дату "+ DateFormat.format.format(date) + " составляет: "+ app.calculateTotalProfit(name, date) + "рублей.";
        }));
        // закупка объекта
        post("/purchase",((request, response) -> {
            ProductFactory productFactory =mapper.readerFor(ProductFactory.class).readValue(request.body());
            try {
                app.getProduct(productFactory.getName(), productFactory.getQuantity(), productFactory.getBuyDate(), productFactory.getBuyPrice());
                return "Товар " + productFactory.getName() + " закуплен";
            }catch (NullPointerException e){
                return "Данила, ты что, крейзи? Такого продукта не существует.";
            }catch (IllegalArgumentException e){
                return "Вы указали отрицательную цену или отрицательное количество";
            }
            catch (Exception e) {
                e.printStackTrace();
                e.getCause();
                return e.getMessage();
            }
        }));
        post("/demand",(((request, response) -> {
            ProductFactory productFactory =mapper.readerFor(ProductFactory.class).readValue(request.body());
            try{app.sellProduct(productFactory.getName(),productFactory.getQuantity(),productFactory.getSellDate(),productFactory.getSellPrice());
                return "Товар "+ productFactory.getName() +" реализован";
            }catch (IllegalArgumentException e){
                return "Вы указали отрицательную цену или отрицательное количество";
            }catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }
        })));


        /* ТЕСТОВЫЕ МЕТОДЫ КОРРЕКТНОЙ РАБОТЫ БД
        app.addProduct("Картошечка");
        app.addProduct("Картошечка");
        Date date = null;
        Date date2= null;
        Date date3=null;
        try {
            date = DateFormat.format.parse("05-02-2019");
            date3 =DateFormat.format.parse("06-03-2019");
            date2=DateFormat.format.parse("01-03-2019");
        } catch (Exception e) {
            e.printStackTrace();
        }
        app.getProduct("Картошечка", 4, date, 400);
        app.getProduct("Картошечка", 2, date, 300);
        app.sellProduct("Картошечка", 2,date2,600);
        app.getProduct("Картошечка", 2, date3, 600);
        app.sellProduct("Картошечка",5, date3, 800);
        System.out.println("Прибыль равна: "+ app.calculateTotalProfit(date3));

         */




    }
    public void addProduct(Product product){
        Session session=factory.openSession();
        Transaction tx=null;
        try {
            tx=session.beginTransaction();
            Query query = session.createQuery("FROM Product where name=:paramName");
            query.setParameter("paramName", product.getName());
            List<Product> products = query.list();
            if(products.size()==0) {
                session.save(product);
                tx.commit();
            }
            else{
                throw new IllegalArgumentException();
            }
        } catch (HibernateException e){
            if(tx!=null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }

    }
    //закупка товара
    public void getProduct(String productName, int quantity, Date buyDate, long buyPrice){
        if(buyPrice<=0||quantity<=0){
            System.out.println("Цена или количество не могут быть отрицательными");
            throw new IllegalArgumentException();
        }
        else {
            Session session = factory.openSession();
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                Query query = session.createQuery("FROM Product");
                List<Product> products = query.list();
                boolean contains = false;
                for (Product product : products) {
                    if (product.getName().equals(productName)) {
                        contains = true;
                    }
                }
                if (contains) {
                    for (int i = 0; i < quantity; i++) {
                        Product product = new Product(productName, buyPrice, buyDate);
                        product.setBought(true);
                        session.save(product);
                    }
                } else {
                    System.out.println("Данила, ты что, крейзи? Такого продукта не существует.");
                    throw new NullPointerException();
                }
                tx.commit();
            } catch (HibernateException e) {
                if (tx != null) tx.rollback();
                e.printStackTrace();
            }
            finally {
                session.close();
            }
        }
    }
    //сбыт товара
    public void sellProduct(String productName, int quantity, Date sellDate, long sellPrice){
        if(sellPrice<=0||quantity<=0){
            System.out.println("Цена или количество не могут быть отрицательными");
            throw new IllegalArgumentException();
        }
        else {
            Session session = factory.openSession();
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                Query query = session.createQuery("FROM Product where name=:paramName and sold=false ORDER BY buyDate");
                query.setParameter("paramName", productName);
                List<Product> products = query.list();
                if (products.size() < quantity) {
                    System.out.println("Столько товара нет в наличии");
                } else {
                    for (int i = 0; i < quantity; i++) {
                        Product product = products.get(i);
                        product.setSellDate(sellDate);
                        product.setSellPrice(sellPrice);
                        product.calculateProfit();
                        product.setSold(true);
                        session.save(product);
                    }
                }
                tx.commit();
            } catch (HibernateException e) {
                if (tx != null) tx.rollback();
                e.printStackTrace();
            //}
            //catch (Exception e) {
             //   System.out.println("Ошибка при сбыте товара");
            } finally {
                session.close();
            }
        }
    }
    //подсчет общей прибыли товара
    public long calculateTotalProfit(String name, Date date){
        Session session=factory.openSession();
        Transaction tx=null;
        long result=0;
        try {
            tx=session.beginTransaction();
            Query query=session.createQuery("FROM Product where name=:param");
            query.setParameter("param",name);
            List<Product> products=query.list();
            for(Product product:products){
                if(product.isSold()&&product.getSellDate().compareTo(date)<=0) {
                    result += product.getProfit();
                }
            }
            tx.commit();
        }catch (HibernateException e){
            if(tx!=null) tx.rollback();
            e.printStackTrace();
        } catch (Exception e){
            System.out.println("Ошибка при подсчете прибыли");
        }finally {
            session.close();
        }
        return result;
    }

    //Не требовались по ТЗ, но работающие методы).
    public void removeProduct(Integer productId){
        Session session=factory.openSession();
        Transaction tx=null;
        try {
            tx=session.beginTransaction();
            Product product= session.get(Product.class, productId);
            session.delete(product);
            tx.commit();
        } catch (HibernateException e){
            if(tx!=null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
    public void removeAll(String tableName) {
        Session session=factory.openSession();
        Transaction tx=null;
        try {
            tx=session.beginTransaction();
            String stringQuery = String.format("DELETE FROM %s", tableName);
            Query query = session.createQuery(stringQuery);
            query.executeUpdate();
            tx.commit();
        } catch (HibernateException e){
            if(tx!=null) tx.rollback();
            e.printStackTrace();
        }
        finally {
            session.close();
        }
    }
}
