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
        String userdir = System.getProperty("user.dir");
        System.out.println("#####user.dir:"+userdir);
        String javahome = System.getProperty("java.home");
        System.out.println("#####java.home:"+javahome);

        System.setProperty("java.home","C:/java");
        String javahomeMod = System.getProperty("java.home");
        System.out.println("#####java.homeMod:"+javahomeMod);

    }
}
