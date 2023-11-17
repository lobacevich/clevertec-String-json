package by.clevertec.lobacevich.util;

import by.clevertec.lobacevich.exception.SerializationException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Deserialize {

    private Deserialize() {
    }

    public static <T> T toObject(String json, Class<T> clazz)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        json = json.replaceAll(" ", "")
                .replaceAll("\n", "")
                .replaceAll("\t", "");
        if (!json.startsWith("{") || !json.endsWith("}")) {
            throw new SerializationException("Invalid format");
        }
        T object = clazz.getDeclaredConstructor().newInstance();
        fillObject(json, object);
        return object;
    }

    private static void fillObject(String json, Object object) throws IllegalAccessException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        json = json.substring(1, json.length() - 1);
        Map<String, String> objectMap = new HashMap<>();
        fillObjectMap(objectMap, json);
        fillFields(objectMap, object);

    }

    private static void fillObjectMap(Map<String, String> objectMap, String json) {
        for (int i = 1; i < json.length(); i++) {
            if (json.charAt(i) == '[' && json.charAt(i - 1) == ':') {
                json = collectionIntoMap(json, objectMap, i);
                i = 0;
                continue;
            }
            if (json.charAt(i) == '{' && json.charAt(i - 1) == ':') {
                json = mapIntoMap(json, objectMap, i);
                i = 0;
                continue;
            }
            if (json.charAt(i) == ',' && json.charAt(i - 1) == '\"') {
                putIntoMap(json.substring(0, i), objectMap);
                json = json.substring(i + 1);
                i = 0;
            }
            if (i == json.length() - 1) {
                putIntoMap(json.substring(0, i + 1), objectMap);
            }
        }
    }

    private static void putIntoMap(String json, Map<String, String> objectMap) {
        int i = json.indexOf(':');
        String key = json.substring(1, i - 1);
        String value = json.substring(i + 1);
        objectMap.put(key, value);
    }

    private static String collectionIntoMap(String json, Map<String, String> objectMap, int start) {
        int counter = 1;
        int i = start + 1;
        while (counter != 0) {
            if (json.charAt(i) == '[') {
                counter++;
            }
            if (json.charAt(i) == ']') {
                counter--;
            }
            i++;
        }
        String key = json.substring(1, start - 2);
        String value = json.substring(start, i);
        objectMap.put(key, value);
        json = json.substring(i);
        if (json.length() > 0) {
            json = json.substring(1);
        }
        return json;
    }

    private static void fillFields(Map<String, String> objectMap, Object object) throws IllegalAccessException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            fillField(objectMap, object, field);
        }
    }

    private static void fillField(Map<String, String> objectMap, Object o, Field field) throws IllegalAccessException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        String type = field.getType().getSimpleName();
        if (type.equals("List")) {
            fillCollectionField(objectMap, o, field);
            return;
        }
        if (type.equals("Map")) {
            System.out.println("Map");
            return;
        }
        String value = objectMap.get(field.getName());
        if (value.equals("\"null\"")) {
            field.set(o, null);
        } else if (type.equals("Double")) {
            field.set(o, Double.valueOf(value));
        } else if (type.equals("Integer")) {
            field.set(o, Integer.valueOf(value));
        } else if (type.equals("UUID")) {
            field.set(o, UUID.fromString(value.substring(1, value.length() - 1)));
        } else if (type.equals("LocalDateTime")) {
            field.set(o, LocalDateTime.parse(value.substring(1, value.length() - 1)));
        } else if (type.equals("LocalDate")) {
            field.set(o, LocalDate.parse(value.substring(1, value.length() - 1)));
        } else if (type.equals("OffsetDateTime")) {
            field.set(o, OffsetDateTime.parse(value.substring(1, value.length() - 1)));
        } else if (type.equals("String")) {
            field.set(o, value.substring(1, value.length() - 1));
        } else {
            System.out.println("Else" + type);
        }
    }

    private static void fillCollectionField(Map<String, String> objectMap, Object o, Field field) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        String genericType = field.getGenericType().getTypeName();
        String className = genericType.split("<")[1].replace(">", "");
        Class<?> clazz = Class.forName(className);
        String value = objectMap.get(field.getName());
        while (value.length() > 1) {
            int counter = 1;
            int i = 2;
            while (counter != 0) {
                if (value.charAt(i) == '{') {
                    counter++;
                }
                if (value.charAt(i) == '}') {
                    counter--;
                }
                i++;
            }
            String json = value.substring(1, i);
            value = value.substring(i, value.length());
            i = 1;
            Object newObject = clazz.getDeclaredConstructor().newInstance();
            fillObject(json, newObject);
            if (field.get(o) == null) {
                List list = new ArrayList();
                field.set(o, list);
                list.add(newObject);
            } else {
                List list = (ArrayList) field.get(o);
                list.add(newObject);
            }
        }
    }

    private static String mapIntoMap(String json, Map<String, String> objectMap, int start) {
        return "";
    }
}
