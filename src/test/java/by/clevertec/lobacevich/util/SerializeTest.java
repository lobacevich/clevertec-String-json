package by.clevertec.lobacevich.util;

import by.clevertec.lobacevich.entity.Customer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SerializeTest {

    Customer customer = TestData.customer;
    ObjectMapper mapper = new ObjectMapper();

    @Test
    void toJson() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        mapper.setDateFormat(dateFormat);
        mapper.registerModule(new JavaTimeModule());
        try {
            String expected = mapper.writeValueAsString(customer);

            String actual = Serialize.toJson(customer)
                    .replaceAll(" ", "")
                    .replaceAll("\n", "")
                    .replaceAll("\t", "");

            assertEquals(expected, actual);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
