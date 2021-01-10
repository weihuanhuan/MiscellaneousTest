package json.jsonp.helper;

import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.JsonWriter;
import javax.json.spi.JsonProvider;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class JsonPHelper {

    private static JsonProvider provider;

    private static Set<Class<?>> REPRESENTABLE;
    private static Map<Class<?>, Class<?>> WRAPPER_MAP;
    private static Map<Class<?>, Class<?>> WRAPPER_MAP_IGNORED;

    static {
        WRAPPER_MAP_IGNORED = new HashMap<>(3);
        WRAPPER_MAP_IGNORED.put(byte.class, Character.class);
        WRAPPER_MAP_IGNORED.put(char.class, Byte.class);
        WRAPPER_MAP_IGNORED.put(void.class, Void.class);

        WRAPPER_MAP = new HashMap<>(9);
        WRAPPER_MAP.put(boolean.class, Boolean.class);
        WRAPPER_MAP.put(short.class, Short.class);
        WRAPPER_MAP.put(int.class, Integer.class);
        WRAPPER_MAP.put(float.class, Float.class);
        WRAPPER_MAP.put(long.class, Long.class);
        WRAPPER_MAP.put(double.class, Double.class);
        WRAPPER_MAP.putAll(WRAPPER_MAP_IGNORED);

        REPRESENTABLE = new HashSet<>();
        REPRESENTABLE.add(String.class);
        REPRESENTABLE.addAll(WRAPPER_MAP.keySet());
        REPRESENTABLE.addAll(WRAPPER_MAP.values());
        REPRESENTABLE.removeAll(WRAPPER_MAP_IGNORED.keySet());
        REPRESENTABLE.removeAll(WRAPPER_MAP_IGNORED.values());
    }

    private static JsonProvider getProvider() {
        if (provider != null) {
            return provider;
        }

        provider = JsonProvider.provider();
        System.out.println(String.format("Found JsonProvider Impl Class:%s.", provider.getClass()));
        return provider;
    }

    public static String generateJsonString(JsonValue jsonValue) {
        JsonProvider provider = getProvider();

        StringWriter stringWriter = new StringWriter();
        JsonWriter writer = provider.createWriter(stringWriter);
        writer.write(jsonValue);
        writer.close();

        String string = stringWriter.toString();
        return string;
    }

    public static JsonValue buildJsonValue(Object instance) throws IntrospectionException, InvocationTargetException, IllegalAccessException {
        if (instance == null) {
            return JsonValue.NULL;
        }

        Class<?> instanceClass = instance.getClass();
        if (instanceClass.isArray()) {
            System.out.println(String.format("Skip unsupported instanceClass for Class:%s.", instanceClass));
            return JsonValue.NULL;
        }

        //only for object
        JsonObjectBuilder objectBuilder = createObjectBuilder(instance);
        JsonObject jsonObject = objectBuilder.build();
        return jsonObject;
    }

    private static JsonObjectBuilder createObjectBuilder(Object instance) throws IntrospectionException, InvocationTargetException, IllegalAccessException {
        JsonProvider provider = getProvider();
        JsonObjectBuilder objectBuilder = provider.createObjectBuilder();
        if (instance == null) {
            return objectBuilder;
        }

        Class<?> instanceClass = instance.getClass();
        BeanInfo beanInfo = Introspector.getBeanInfo(instanceClass);

        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            Class<?> propertyType = propertyDescriptor.getPropertyType();
            //这是 jdk 内部的对象，其只有一个 native 的 readMethod 而没有 write 方法。
            // java.beans.PropertyDescriptor[name=class; propertyType=class java.lang.Class;
            // readMethod=public final native java.lang.Class java.lang.Object.getClass()]
            if (Class.class == propertyType) {
//                System.out.println(String.format("Skip propertyType Class:%s.", propertyType));
                continue;
            }

            Object value = null;
            String name = propertyDescriptor.getName();
            Method readMethod = propertyDescriptor.getReadMethod();
            if (readMethod != null) {
                readMethod.setAccessible(true);
                value = readMethod.invoke(instance, null);
            }

            if (isRepresentable(propertyType)) {
                addJsonValue(objectBuilder, name, value);
            } else if (!propertyType.isPrimitive()) {
                //区分一个对象的域是完全没有进行初始化赋值，还是这个对象赋值了但是没有对其进行属性配置。
                //handle for instance internal field object that is null.
                //最终产生的 json 字符串中这些 null 对象由 javax.json.JsonValue.NULL 来表示，可还原为 null 引用。
                //handle for instance internal field object that is not null.
                //最终产生的 json 字符串中这些 not null 对象由 javax.json.JsonObject 来表示， 可还原为所有域值为默认值的 not null 对象。
                JsonValue jsonValue = buildJsonValue(value);
                objectBuilder.add(name, jsonValue);
            }
        }
        return objectBuilder;
    }

    private static boolean isRepresentable(Class<?> clazz) {
        return REPRESENTABLE.contains(clazz);
    }

    private static void addJsonValue(JsonObjectBuilder objectBuilder, String name, Object object) {
        if (object == null) {
            objectBuilder.addNull(name);
            return;
        }

        Class<?> type = object.getClass();
        Class<?> wrapperType = type;
        if (object.getClass().isPrimitive()) {
            wrapperType = WRAPPER_MAP.get(type);
        }

        if (wrapperType == Boolean.class) {
            objectBuilder.add(name, (Boolean) object);
        } else if (wrapperType == Short.class) {
            objectBuilder.add(name, (Integer) object);
        } else if (wrapperType == Integer.class) {
            objectBuilder.add(name, (Integer) object);
        } else if (wrapperType == Float.class) {
            objectBuilder.add(name, (Double) object);
        } else if (wrapperType == Long.class) {
            objectBuilder.add(name, (Long) object);
        } else if (wrapperType == Double.class) {
            objectBuilder.add(name, (Double) object);
        } else if (wrapperType == String.class) {
            objectBuilder.add(name, (String) object);
        } else {
            System.out.println(String.format("addNull due to unsupported type for:%s with object:%s.", name, object));
            objectBuilder.addNull(name);
        }
    }

    public static JsonValue parseJsonString(String string) {
        if (string == null) {
            return JsonValue.NULL;
        }
        JsonProvider provider = getProvider();

        StringReader stringReader = new StringReader(string);
        JsonReader jsonReader = provider.createReader(stringReader);
        JsonValue jsonValue = jsonReader.readValue();
        return jsonValue;
    }

    public static Object buildInstance(JsonValue jsonValue, Class<?> instanceClass) throws IntrospectionException, IOException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, InstantiationException {
        if (jsonValue == null) {
            return null;
        }

        JsonValue.ValueType valueType = jsonValue.getValueType();
        switch (valueType) {
            case OBJECT:
                //only for object
                JsonObject jsonObject = jsonValue.asJsonObject();
                Object instance = parseJsonObject(jsonObject, instanceClass);
                return instance;
            default:
                System.out.println(String.format("Skip unsupported JsonStructure for JsonValue:%s.", jsonValue));
                return null;
        }
    }

    private static Object parseJsonObject(JsonObject jsonObject, Class<?> instanceClass) throws IntrospectionException, IOException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, InstantiationException {
        if (jsonObject == null) {
            return null;
        }

        BeanInfo beanInfo = Introspector.getBeanInfo(instanceClass);
        Object instance = instanceClass.newInstance();

        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            Class<?> propertyType = propertyDescriptor.getPropertyType();
            if (Class.class == propertyType) {
//                System.out.println(String.format("Skip propertyType Class:%s.", propertyType));
                continue;
            }

            Method writeMethod = propertyDescriptor.getWriteMethod();
            if (writeMethod == null) {
                System.out.println(String.format("Skip writeMethod == null for Class:%s.", propertyType));
                continue;
            }

            String name = propertyDescriptor.getName();
            JsonValue jsonValue = jsonObject.get(name);
            if (jsonValue == null) {
                System.out.println(String.format("Skip JsonValue == null for Class:%s.", propertyType));
                continue;
            }

            Object propertyValue;
            JsonValue.ValueType valueType = jsonValue.getValueType();
            if (isResolvable(valueType)) {
                propertyValue = resolvedJsonValue(jsonValue, propertyType);
            } else {
                propertyValue = buildInstance(jsonValue, propertyType);
            }
            writeMethod.setAccessible(true);
            writeMethod.invoke(instance, propertyValue);
        }
        return instance;
    }


    private static boolean isResolvable(JsonValue.ValueType valueType) {
        switch (valueType) {
            case NULL:
            case TRUE:
            case FALSE:
            case NUMBER:
            case STRING:
                return true;
            default:
                return false;
        }
    }

    private static Object resolvedJsonValue(JsonValue jsonvalue, Class<?> type) {
        if (jsonvalue == null) {
            return null;
        }

        JsonValue.ValueType valueType = jsonvalue.getValueType();
        switch (valueType) {
            case NULL:
                return null;
            case TRUE:
                return Boolean.TRUE;
            case FALSE:
                return Boolean.FALSE;
            case STRING:
                JsonString jsonString = (JsonString) jsonvalue;
                return jsonString.getString();
            case NUMBER:
                JsonNumber jsonNumber = (JsonNumber) jsonvalue;
                Number number = jsonNumber.bigDecimalValue();

                Class<?> wrapperType = type;
                if (type.isPrimitive()) {
                    wrapperType = WRAPPER_MAP.get(type);
                }

                if (wrapperType == Short.class) {
                    return number.shortValue();
                } else if (wrapperType == Integer.class) {
                    return number.intValue();
                } else if (wrapperType == Float.class) {
                    return number.floatValue();
                } else if (wrapperType == Long.class) {
                    return number.longValue();
                } else if (wrapperType == Double.class) {
                    return number.doubleValue();
                }
            default:
                System.out.println(String.format("resolvedJsonValue Null due to unsupported JsonValue for:%s with object:%s.", jsonvalue));
                return null;
        }
    }

}
