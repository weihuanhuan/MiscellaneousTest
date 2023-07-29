package spring.jdbc.mybatis.bean;

import java.io.Serializable;

public class MybatisRedis implements Serializable {

    private Integer id;

    private String name;
    private String groupName;

    private String mode;
    private String role;
    private String masterName;

    private String node;
    private String configTemplate;
    private String configText;
    private String installPath;

    private String status;
    private int statusVersion;

    private String oldServerHost;
    private Integer oldServerPort;
    private String oldRequirePass;

    private String version;
    private String properties;

    public void copy(MybatisRedis other) {
        this.id = other.id;
        this.name = other.name;
        this.groupName = other.groupName;
        this.mode = other.mode;
        this.role = other.role;
        this.masterName = other.masterName;
        this.node = other.node;
        this.configTemplate = other.configTemplate;
        this.configText = other.configText;
        this.installPath = other.installPath;
        this.status = other.status;
        this.statusVersion = other.statusVersion;
        this.oldServerHost = other.oldServerHost;
        this.oldServerPort = other.oldServerPort;
        this.oldRequirePass = other.oldRequirePass;
        this.version = other.version;
        this.properties = other.properties;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getMasterName() {
        return masterName;
    }

    public void setMasterName(String masterName) {
        this.masterName = masterName;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public String getConfigTemplate() {
        return configTemplate;
    }

    public void setConfigTemplate(String configTemplate) {
        this.configTemplate = configTemplate;
    }

    public String getConfigText() {
        return configText;
    }

    public void setConfigText(String configText) {
        this.configText = configText;
    }

    public String getInstallPath() {
        return installPath;
    }

    public void setInstallPath(String installPath) {
        this.installPath = installPath;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getStatusVersion() {
        return statusVersion;
    }

    public void setStatusVersion(int statusVersion) {
        this.statusVersion = statusVersion;
    }

    public String getOldServerHost() {
        return oldServerHost;
    }

    public void setOldServerHost(String oldServerHost) {
        this.oldServerHost = oldServerHost;
    }

    public Integer getOldServerPort() {
        return oldServerPort;
    }

    public void setOldServerPort(Integer oldServerPort) {
        this.oldServerPort = oldServerPort;
    }

    public String getOldRequirePass() {
        return oldRequirePass;
    }

    public void setOldRequirePass(String oldRequirePass) {
        this.oldRequirePass = oldRequirePass;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    @Override
    public String toString() {
        return "Redis{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", configText='" + configText + '\'' +
                '}';
    }
}
