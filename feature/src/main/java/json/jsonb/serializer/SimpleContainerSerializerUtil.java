package json.jsonb.serializer;

public class SimpleContainerSerializerUtil {

    private static boolean usingJsonbDeserializer = true;

    public static boolean isUsingJsonbDeserializer() {
        return usingJsonbDeserializer;
    }

    public static void setUsingJsonbDeserializer(boolean usingJsonbDeserializer) {
        SimpleContainerSerializerUtil.usingJsonbDeserializer = usingJsonbDeserializer;
    }

}
