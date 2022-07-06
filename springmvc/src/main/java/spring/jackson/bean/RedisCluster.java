package spring.jackson.bean;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

//avoid infinite loop within RedisBaseDeserializer
@JsonDeserialize(using = JsonDeserializer.None.class)
public class RedisCluster extends RedisMaster {

    private String clusterEnabled;

    public String getClusterEnabled() {
        return clusterEnabled;
    }

    public void setClusterEnabled(String clusterEnabled) {
        this.clusterEnabled = clusterEnabled;
    }

    @Override
    public String toString() {
        return "RedisCluster{" + "clusterEnabled='" + clusterEnabled + '\'' + '}';
    }

}
