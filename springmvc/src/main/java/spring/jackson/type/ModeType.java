package spring.jackson.type;

public enum ModeType {

    CLUSTER("cluster"), MASTER_SLAVE("master-salve"), SENTINEL("sentinel"), UNKNOWN("unknown");

    private final String mode;

    ModeType(String mode) {
        this.mode = mode;
    }

    @Override
    public String toString() {
        return mode;
    }

    public static ModeType from(String mode) {
        for (ModeType modeType : ModeType.values()) {
            if (modeType.mode.equalsIgnoreCase(mode)) {
                return modeType;
            }
        }
        return UNKNOWN;
    }

}
