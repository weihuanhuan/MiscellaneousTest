package reflect.field;

import java.lang.reflect.Field;

/**
 * Created by JasonFitch on 11/27/2019.
 */
public class FieldTest {

    String name = "hello";
    int countInt = 3;
    Integer countInteger = 5;
    boolean enableBool = true;
    Boolean enableBoolean = Boolean.FALSE;

    public static void main(String[] args) throws IllegalAccessException {

        FieldTest fieldTest = new FieldTest();

        for (Field field : fieldTest.getClass().getDeclaredFields()) {
            field.setAccessible(true);

            System.out.println("########################");
            String name = field.getName();

            Class<?> type = field.getType();
            System.out.println(type);

            if (type.isAssignableFrom(String.class)) {
                field.set(fieldTest, "newHello");
                System.out.println("String:" + name);

            } else if (type.isAssignableFrom(int.class)) {
                System.out.println("int:" + name);

            } else if (type.isAssignableFrom(Integer.class)) {
                System.out.println("Integer:" + name);

            } else if (type.isAssignableFrom(boolean.class)) {
                System.out.println("boolean:" + name);

            } else if (type.isAssignableFrom(Boolean.class)) {
                System.out.println("Boolean:" + name);
            }
        }


        System.out.println("########################");
        System.out.println(fieldTest.getName());
    }

    public String getName() {
        return name;
    }

    public int getCountInt() {
        return countInt;
    }

    public Integer getCountInteger() {
        return countInteger;
    }

    public boolean isEnableBool() {
        return enableBool;
    }

    public Boolean getEnableBoolean() {
        return enableBoolean;
    }
}
