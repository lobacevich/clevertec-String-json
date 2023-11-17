package by.clevertec.lobacevich.runner;

import by.clevertec.lobacevich.entity.Customer;
import by.clevertec.lobacevich.entity.Order;
import by.clevertec.lobacevich.entity.Product;
import by.clevertec.lobacevich.util.Deserialize;
import by.clevertec.lobacevich.util.Serialize;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;


public class Main {

    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        Product product1 = new Product(UUID.randomUUID(), "Hamburger", 7.52);
        Product product2 = new Product(UUID.randomUUID(), "Juice", 6.35);
        Product product3 = new Product(UUID.randomUUID(), "Fries", 7.02);
        Product product4 = new Product(UUID.randomUUID(), "Coca", 5.39);

        Order order1 = new Order(UUID.randomUUID(), List.of(product1, product3, product4),
                OffsetDateTime.now());
        Order order2 = new Order(UUID.randomUUID(), List.of(product2, product1, product4),
                OffsetDateTime.now());

        Customer customer = new Customer(UUID.randomUUID(), "Alex", "Petrov",
                LocalDate.of(1997, 05, 21), List.of(order1, order2));

        String json = Serialize.toJson(customer);
        System.out.println(json);
        System.out.println(Deserialize.toObject(json, Customer.class));
    }
}
