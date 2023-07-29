package spring.jdbc.aop.realtime.updated;

public class UpdatedConfig {

    private final String name;
    private final String mode;
    private final String role;

    private String oldConfigText;
    private String newConfigText;

    public UpdatedConfig(String name, String mode, String role) {
        this.name = name;
        this.mode = mode;
        this.role = role;
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
                ", oldConfigText='" + oldConfigText + '\'' +
                ", newConfigText='" + newConfigText + '\'' +
                '}';
    }

}
