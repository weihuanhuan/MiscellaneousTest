package spring.jackson.bean;

import java.io.Serializable;

public class Redis implements Serializable {

    private String name;

    private String mode;

    private String role;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @Override
    public String toString() {
        return "Redis{" + "name='" + name + '\'' + ", mode='" + mode + '\'' + ", role='" + role + '\'' + '}';
    }
}
