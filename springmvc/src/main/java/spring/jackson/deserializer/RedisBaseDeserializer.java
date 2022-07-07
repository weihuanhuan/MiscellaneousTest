package spring.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import spring.jackson.bean.RedisBase;
import spring.jackson.type.RedisBaseType;

import java.io.IOException;

public class RedisBaseDeserializer extends JsonDeserializer<RedisBase> {

    private static final String MODE_FIELD_NAME = "mode";

    private static final String ROLE_FIELD_NAME = "role";

    @Override
    public RedisBase deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        // Get reference to ObjectCodec
        ObjectCodec codec = jsonParser.getCodec();

        // Parse "object" node into Jackson's tree model
        JsonNode jsonNode = codec.readTree(jsonParser);
        if (jsonNode.isNull()) {
            return null;
        }

        checkJsonObjectField(jsonParser, jsonNode, MODE_FIELD_NAME);
        checkJsonObjectField(jsonParser, jsonNode, ROLE_FIELD_NAME);

        // Get value of the "mode" and "role" property
        String mode = jsonNode.get(MODE_FIELD_NAME).asText();
        String role = jsonNode.get(ROLE_FIELD_NAME).asText();

        // Check the "mode" and "role" property and map "object" to the suitable class
        RedisBaseType form = RedisBaseType.form(mode, role);
        Class<? extends RedisBase> redisClazz = form.getRedisClazz();
        return codec.treeToValue(jsonNode, redisClazz);
    }

    private void checkJsonObjectField(JsonParser jsonParser, JsonNode jsonNode, String fieldName) throws JsonMappingException {
        if (!jsonNode.has(fieldName)) {
            throwJsonMappingException(jsonParser, fieldName);
        }
    }

    private void throwJsonMappingException(JsonParser jsonParser, String fieldName) throws JsonMappingException {
        String format = String.format("missing field named [%s]!", fieldName);
        throw new JsonMappingException(jsonParser, format);
    }

}