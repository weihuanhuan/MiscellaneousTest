package spring.jackson.type;

public enum RoleType {

    MASTER("master"), REPLICA("replica"), SENTINEL("sentinel"), UNKNOWN("unknown");

    private final String role;

    RoleType(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return role;
    }

    public static RoleType from(String role) {
        for (RoleType roleType : RoleType.values()) {
            if (roleType.role.equalsIgnoreCase(role)) {
                return roleType;
            }
        }
        return UNKNOWN;
    }

}
