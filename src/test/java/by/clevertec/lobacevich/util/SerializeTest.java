package by.clevertec.lobacevich.util;

import by.clevertec.lobacevich.entity.Customer;
import by.clevertec.lobacevich.exception.SerializationException;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SerializeTest {

    @Test
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSXXXXX")
    void toJson() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        mapper.setDateFormat(dateFormat);
        mapper.registerModule(new JavaTimeModule());
        Customer customer = ProductTestData.createCustomer();
        try {
            String expected = mapper.writeValueAsString(customer);

            String actual = Serialize.toJson(customer)
                    .replaceAll("\": ", "\":")
                    .replaceAll("\n", "")
                    .replaceAll("\t", "");

            assertEquals(expected, actual);
            System.out.println(expected);
            System.out.println(actual);
        } catch (JsonProcessingException e) {
            throw new SerializationException(e.getMessage());
        }
    }
}
