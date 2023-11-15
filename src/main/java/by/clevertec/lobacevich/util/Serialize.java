package by.clevertec.lobacevich.util;

import by.clevertec.lobacevich.exception.SerializationException;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Serialize {

    private static final List<String> DOUBLE_QUOTE = List.of("String", "LocalDateTime", "LocalDate",
            "OffsetDatetime", "UUID");
    private static int counter = 0;
    private static final StringBuilder BUILDER = new StringBuilder();

    private Serialize() {
    }

    public static String toJson(Object o) {
        converter(o);
        return BUILDER.toString();
    }

    private static void converter(Object o) {
        counter++;
        if (o instanceof Map<?, ?>) {
            MapConverter(o);
            System.out.println("Map");
            return;
        }
        if (o instanceof Collection<?> ||
                o.getClass().isArray()) {
            CollectionConverter(o);
            return;
        }
        BUILDER.append("{\n");
        Class<?> clazz = o.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            fieldConverter(field, o);
        }
        BUILDER.deleteCharAt(BUILDER.length() - 2);
        BUILDER.append("}\n");
        counter--;
    }

    private static void fieldConverter(Field field, Object o) {
        field.setAccessible(true);
        BUILDER.append("\t".repeat(counter) + "\"")
                .append(field.getName())
                .append("\": ");
        try {
            Object value = field.get(o);
            valueConverter(value);
        } catch (IllegalAccessException e) {
            throw new SerializationException(e.getMessage());
        }
    }

    private static void valueConverter(Object value) {
        if (value == null) {
            BUILDER.append("null")
                    .append(",\n");
        } else if (value.getClass().getSuperclass().getSimpleName().equals("Number") ||
                value.getClass().getSimpleName().equals("Boolean") ||
                value.getClass().getSimpleName().equals("Character") ||
                value.getClass().isPrimitive()) {
            BUILDER.append(value)
                    .append(",\n");
        } else if (DOUBLE_QUOTE.contains(value.getClass().getSimpleName())) {
            BUILDER.append("\"")
                    .append(value)
                    .append("\",\n");
        } else {
            converter(value);
        }
    }

    private static void CollectionConverter(Object o) {
        BUILDER.append(" [\n");
        for (Object elem : (Collection<?>) o) {
            converter(elem);
            BUILDER.append(",");
        }
        BUILDER.deleteCharAt(BUILDER.length() - 1);
        BUILDER.append("],\n");
    }

    private static void MapConverter(Object o) {
    }
}
