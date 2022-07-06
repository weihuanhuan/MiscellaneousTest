package spring.jackson.bean;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = JsonDeserializer.None.class)
public class RedisMaster extends RedisBase {

    private String appendOnly;

    public String getAppendOnly() {
        return appendOnly;
    }

    public void setAppendOnly(String appendOnly) {
        this.appendOnly = appendOnly;
    }

    @Override
    public String toString() {
        return "RedisMaster{" + "appendOnly='" + appendOnly + '\'' + '}';
    }

}
