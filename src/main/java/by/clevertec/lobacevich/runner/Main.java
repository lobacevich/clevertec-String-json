package by.clevertec.lobacevich.runner;

import by.clevertec.lobacevich.entity.Customer;
import by.clevertec.lobacevich.entity.Order;
import by.clevertec.lobacevich.entity.Product;
import by.clevertec.lobacevich.util.Serialize;

import java.time.Clock;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public class Main {

    public static void main(String[] args) {
        Customer customer = Customer.builder()
                .id(UUID.randomUUID())
                .firstName("Alex")
                .lastName("Petrov")
                .dateBirth(LocalDate.now())
                .orders(List.of(Order.builder()
                        .id(UUID.randomUUID())
                        .products(List.of(Product.builder()
                                        .id(UUID.randomUUID())
                                        .name("Bread")
                                        .price(7.52)
                                        .build(),
                                Product.builder()
                                        .id(UUID.randomUUID())
                                        .name("Milk")
                                        .price(5.37)
                                        .build()))
                        .build()))
                .build();
        System.out.println(Serialize.toJson(customer));
    }
}
