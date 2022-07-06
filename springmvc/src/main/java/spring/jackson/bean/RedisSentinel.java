package spring.jackson.bean;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = JsonDeserializer.None.class)
public class RedisSentinel extends RedisBase {

    @Override
    public String toString() {
        return "RedisSentinel{}";
    }

}
