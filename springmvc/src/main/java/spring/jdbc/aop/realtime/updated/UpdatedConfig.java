package spring.jdbc.aop.realtime.updated;

public class UpdatedConfig {

    private final String name;
    private final String mode;
    private final String role;
    private final String status;

    private String oldConfigText;
    private String newConfigText;

    public UpdatedConfig(String name, String mode, String role, String status) {
        this.name = name;
        this.mode = mode;
        this.role = role;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getMode() {
        return mode;
    }

    public String getRole() {
        return role;
    }

    public String getStatus() {
        return status;
    }

    public String getOldConfigText() {
        return oldConfigText;
    }

    public void setOldConfigText(String oldConfigText) {
        this.oldConfigText = oldConfigText;
    }

    public String getNewConfigText() {
        return newConfigText;
    }

    public void setNewConfigText(String newConfigText) {
        this.newConfigText = newConfigText;
    }

    @Override
    public String toString() {
        return "UpdatedConfig{" +
                "name='" + name + '\'' +
                ", mode='" + mode + '\'' +
                ", role='" + role + '\'' +
                ", status='" + status + '\'' +
                ", oldConfigText='" + oldConfigText + '\'' +
                ", newConfigText='" + newConfigText + '\'' +
                '}';
    }

}
