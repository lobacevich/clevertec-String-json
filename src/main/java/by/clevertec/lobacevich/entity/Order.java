package by.clevertec.lobacevich.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class Order {

    private UUID id;
    private List<Product> products;
//    private OffsetDateTime createDate;
}
