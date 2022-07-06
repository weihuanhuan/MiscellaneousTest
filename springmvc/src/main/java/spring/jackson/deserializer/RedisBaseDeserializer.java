package spring.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import spring.jackson.bean.RedisBase;
import spring.jackson.type.RedisBaseType;

import java.io.IOException;

public class RedisBaseDeserializer extends JsonDeserializer<RedisBase> {

    @Override
    public RedisBase deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        // Get reference to ObjectCodec
        ObjectCodec codec = p.getCodec();

        // Parse "object" node into Jackson's tree model
        JsonNode node = codec.readTree(p);

        // Get value of the "mode" and "role" property
        String mode = node.get("mode").asText();
        String role = node.get("role").asText();

        // Check the "mode" and "role" property and map "object" to the suitable class
        RedisBaseType form = RedisBaseType.form(mode, role);
        Class<? extends RedisBase> redisClazz = form.getRedisClazz();
        return codec.treeToValue(node, redisClazz);
    }

}