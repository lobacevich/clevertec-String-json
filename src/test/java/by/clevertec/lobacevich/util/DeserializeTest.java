package by.clevertec.lobacevich.util;

import by.clevertec.lobacevich.entity.Customer;
import by.clevertec.lobacevich.exception.SerializationException;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class DeserializeTest {

    @Test
    void toObject() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        mapper.setDateFormat(dateFormat);
        mapper.registerModule(new JavaTimeModule());
        String json = ProductTestData.getJson();
        try {
            Customer expected = mapper.readValue(json, Customer.class);

            Customer actual = Deserialize.toObject(json, Customer.class);

            assertEquals(expected, actual);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException |
                 ClassNotFoundException | JsonProcessingException e) {
            throw new SerializationException(e.getMessage());
        }
    }
}
