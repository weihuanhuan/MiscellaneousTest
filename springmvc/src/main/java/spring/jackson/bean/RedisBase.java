package spring.jackson.bean;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import spring.jackson.deserializer.RedisBaseDeserializer;

@JsonDeserialize(using = RedisBaseDeserializer.class)
public class RedisBase extends Redis {

    private String serverHost;

    private Integer serverPort;

    private String requirePass;

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public Integer getServerPort() {
        return serverPort;
    }

    public void setServerPort(Integer serverPort) {
        this.serverPort = serverPort;
    }

    public String getRequirePass() {
        return requirePass;
    }

    public void setRequirePass(String requirePass) {
        this.requirePass = requirePass;
    }
}
