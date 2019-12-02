package json;

/**
 * Created by JasonFitch on 11/27/2019.
 */
public class Info {

    String name = "hello";
    int countInt = 3;
    Integer countInteger = 5;
    boolean enableBool = true;
    Boolean enableBoolean = Boolean.FALSE;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCountInt() {
        return countInt;
    }

    public void setCountInt(int countInt) {
        this.countInt = countInt;
    }

    public Integer getCountInteger() {
        return countInteger;
    }

    public void setCountInteger(Integer countInteger) {
        this.countInteger = countInteger;
    }

    public boolean getEnableBool() {
        return enableBool;
    }

    public void setEnableBool(boolean enableBool) {
        this.enableBool = enableBool;
    }

    public Boolean getEnableBoolean() {
        return enableBoolean;
    }

    public void setEnableBoolean(Boolean enableBoolean) {
        this.enableBoolean = enableBoolean;
    }

    @Override
    public String toString() {
        return "Info{" +
                "name='" + name + '\'' +
                ", countInt=" + countInt +
                ", countInteger=" + countInteger +
                ", enableBool=" + enableBool +
                ", enableBoolean=" + enableBoolean +
                '}';
    }
}
