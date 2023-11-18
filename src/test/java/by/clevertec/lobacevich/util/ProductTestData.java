package by.clevertec.lobacevich.util;

import by.clevertec.lobacevich.entity.Customer;
import by.clevertec.lobacevich.entity.Order;
import by.clevertec.lobacevich.entity.Product;
import lombok.Data;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class ProductTestData {

    public static Customer createCustomer() {
        Product product1 = new Product(UUID.randomUUID(), "Hamburger", 7.52);
        Product product2 = new Product(UUID.randomUUID(), "Juice", 6.35);
        Product product3 = new Product(UUID.randomUUID(), "Fries", 7.02);
        Product product4 = new Product(UUID.randomUUID(), "Coca", 5.39);

        Order order1 = new Order(UUID.randomUUID(), List.of(product1, product3, product4),
                OffsetDateTime.parse("2023-11-18T12:33:30Z"));
        Order order2 = new Order(UUID.randomUUID(), List.of(product2, product1, product4),
                OffsetDateTime.parse("2023-11-18T12:33:30Z"));

        Customer customer = new Customer(UUID.randomUUID(), "Alex", "Petrov",
                LocalDate.of(1997, 05, 21),
                Map.of(LocalDate.of(2003, 05, 19), List.of(order1, order2)));
        return customer;
    }
    public static String getJson() {
        return Serialize.toJson(createCustomer())
                .replaceAll("\": ", "\":")
                .replaceAll("\n", "")
                .replaceAll("\t", "");
    }
}
