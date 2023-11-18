package by.clevertec.lobacevich.util;

import by.clevertec.lobacevich.exception.SerializationException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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

    private static String mapIntoMap(String json, Map<String, String> objectMap, int start) {
        int counter = 1;
        int i = start + 1;
        while (counter != 0) {
            if (json.charAt(i) == '{') {
                counter++;
            }
            if (json.charAt(i) == '}') {
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
        if (object.getClass().getSimpleName().equals("List")) {
            return;
        }
        for (Field field : fields) {
            field.setAccessible(true);
            fillField(objectMap, object, field);
        }
    }

    private static void fillField(Map<String, String> objectMap, Object o, Field field) throws IllegalAccessException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        String type = field.getType().getSimpleName();
        if (type.equals("List")) {
            fillListField(objectMap, o, field);
            return;
        }
        if (type.equals("Map")) {
            fillMapField(objectMap, o, field);
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

    private static void fillListField(Map<String, String> objectMap, Object o, Field field) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> clazz = getListGenericClass(o, field);
        String value = objectMap.get(field.getName());
        List list = new ArrayList();
        field.set(o, list);
        fillList(list, value, clazz);
    }

    private static Class<?> getListGenericClass(Object o, Field field) throws ClassNotFoundException {
        String genericType = field.getGenericType().getTypeName();
        String className = genericType.split("<")[1].replace(">", "");
        Class<?> clazz = Class.forName(className);
        return clazz;
    }


    private static void fillList(List list, String value, Class<?> clazz) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
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
                list.add(newObject);
        }
    }

    private static void fillMapField(Map<String, String> objectMap, Object o, Field field) throws ClassNotFoundException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        ParameterizedType type = (ParameterizedType) field.getGenericType();
        Type mapKeyType = type.getActualTypeArguments()[0];
        Type mapValueType = type.getActualTypeArguments()[1];
        String json = objectMap.get(field.getName());
        int index = json.indexOf(':');
        String keyJson = json.substring(1, index);
        String valueJson = json.substring(index + 1, json.length() - 1);
        Object keyObject = createObjectFromType(mapKeyType, keyJson);
        Object valueObject = createObjectFromType(mapValueType, valueJson);
        Map map = new HashMap<>();
        map.put(keyObject, valueObject);
        field.set(o, map);
    }

    private static Object createObjectFromType(Type type, String json) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (type.toString().contains("List")) {
            List list = new ArrayList<>();
            String className = type.toString().split("<")[1].replace(">", "");
            Class<?> clazz = Class.forName(className);
            fillList(list, json, clazz);
            return list;
        } else if (type.getClass().getSimpleName().equals("Map")) {
            Map map = new HashMap();
            fillObject(json, map);
            return map;
        } else if (((Class) type).getSimpleName().equals("Double")) {
            return Double.valueOf(json);
        } else if (((Class) type).getSimpleName().equals("Integer")) {
            return Integer.valueOf(json);
        } else if (((Class) type).getSimpleName().equals("UUID")) {
            return UUID.fromString(json.substring(1, json.length() - 1));
        } else if (((Class) type).getSimpleName().equals("LocalDateTime")) {
            return LocalDateTime.parse(json.substring(1, json.length() - 1));
        } else if (((Class) type).getSimpleName().equals("LocalDate")) {
            return LocalDate.parse(json.substring(1, json.length() - 1));
        } else if (((Class) type).getSimpleName().equals("OffsetDateTime")) {
            return OffsetDateTime.parse(json.substring(1, json.length() - 1));
        } else if (((Class) type).getSimpleName().equals("String")) {
            return json.substring(1, json.length() - 1);
        } else {
            Constructor<?> constructor = type.getClass().getDeclaredConstructor();
            constructor.setAccessible(true);
            Object o = constructor.newInstance();
            fillObject(json, o);
            return o;
        }
    }
}
