package SystemTest;

import java.util.Enumeration;

public class SystemTest {

    public static void main(String[] args) {

        Enumeration<?> enumeration = System.getProperties().propertyNames();
        while (enumeration.hasMoreElements()) {
            Object name = enumeration.nextElement();
            System.out.println(name + " = " + System.getProperty((String) name));
        }
        System.out.println("------------------------------------------------");
        System.out.println(System.getProperty(("user.dir")));

    }

}
