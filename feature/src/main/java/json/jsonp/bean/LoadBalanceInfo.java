package json.jsonp.bean;

import java.io.Serializable;
import java.util.Objects;

public class LoadBalanceInfo implements Serializable {

    public static final String SPARK_LB_MEMBER_DETAILS_KEY = "SPARK_LB_Endpoints";

    private String instanceName;
    private int weight;
    private ConnectionInfo sparkConnectionInfo;
    private ConnectionInfo httpConnectionInfo;

    public LoadBalanceInfo() {
    }

    public String getInstanceName() {
        return this.instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public int getWeight() {
        return this.weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public ConnectionInfo getHttpConnectionInfo() {
        return this.httpConnectionInfo;
    }

    public void setHttpConnectionInfo(ConnectionInfo httpConnectionInfo) {
        this.httpConnectionInfo = httpConnectionInfo;
    }

    public ConnectionInfo getSparkConnectionInfo() {
        return this.sparkConnectionInfo;
    }

    public void setSparkConnectionInfo(ConnectionInfo sparkConnectionInfo) {
        this.sparkConnectionInfo = sparkConnectionInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoadBalanceInfo that = (LoadBalanceInfo) o;
        return weight == that.weight &&
                Objects.equals(instanceName, that.instanceName) &&
                Objects.equals(sparkConnectionInfo, that.sparkConnectionInfo) &&
                Objects.equals(httpConnectionInfo, that.httpConnectionInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instanceName, weight, sparkConnectionInfo, httpConnectionInfo);
    }

    private byte aByte;
    private char aChar;

    public byte getaByte() {
        return aByte;
    }

    public void setByte(byte b) {
        this.aByte = b;
    }

    public char getaChar() {
        return aChar;
    }

    public void setChar(char c) {
        this.aChar = c;
    }
}
