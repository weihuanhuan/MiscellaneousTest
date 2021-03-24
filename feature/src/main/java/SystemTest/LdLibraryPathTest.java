package SystemTest;

import java.io.File;
import java.util.StringTokenizer;

public class LdLibraryPathTest {

    public static void main(String[] args) {

        String pathSeparator = File.pathSeparator;

        System.out.println("################# testJavaLibraryPath ##########################");
        testJavaLibraryPath(pathSeparator);

        System.out.println("################# testEnvPath ##########################");
        testEnvPath(pathSeparator);

        System.out.println("################# testEnvLdLibraryPath ##########################");
        testEnvLdLibraryPath(pathSeparator);
    }

    public static void testJavaLibraryPath(String delimiter) {
        String javaLibraryPath = System.getProperty("java.library.path");
        printProperty(javaLibraryPath, delimiter);
    }

    public static void testEnvPath(String delimiter) {
        String path = System.getenv("PATH");
        printProperty(path, delimiter);
    }

    public static void testEnvLdLibraryPath(String delimiter) {
        String ldLibraryLibEnv = System.getenv("LD_LIBRARY_LIB");
        printProperty(ldLibraryLibEnv, delimiter);
    }

    public static void printProperty(String property, String delimiter) {
        if (property == null) {
            return;
        }

        StringTokenizer parser = new StringTokenizer(property, delimiter);
        while (parser.hasMoreTokens()) {
            System.out.println(parser.nextToken());
        }
    }

}
