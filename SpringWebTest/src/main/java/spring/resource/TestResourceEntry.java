package spring.resource;

import java.net.URL;

/**
 * Created by JasonFitch on 5/9/2019.
 */
public class TestResourceEntry {

    private static String resourceName = "/spring/resource/TestResourceEntry.class";

    public static void main(String[] args) {
        String clazzName = resourceToClazz(resourceName);
        System.out.println(clazzName);
    }

    public static String getResourceName() {
        return resourceName;
    }

    public static String getClazzName() {
        return resourceToClazz(resourceName);
    }

    static {

        System.out.println("############## class information start #####################");

        System.out.println("----------------- class location info ------------------");
        Class<TestResourceEntry> clazz = TestResourceEntry.class;
        URL location = clazz.getProtectionDomain().getCodeSource().getLocation();
        System.out.println(location);

        System.out.println("----------------- classloader hierarchy ------------------");
        ClassLoader classLoader = clazz.getClassLoader();
        System.out.println(classLoader);
        ClassLoader parent = classLoader.getParent();
        while (parent != null) {
            System.out.println(parent);
            parent = parent.getParent();
        }

        System.out.println("############## class information end #####################");

    }

    public static String resourceToClazz(String resourceName) {
        if (resourceName == null) {
            return "";
        }
        String clazzName = resourceName.substring(0, resourceName.lastIndexOf("."));
        if (clazzName.startsWith("/")) {
            clazzName = clazzName.replaceAll("/", ".").replaceFirst("\\.", "");
        } else {
            clazzName = clazzName.replaceAll("/", ".");
        }
        return clazzName;
    }
}
