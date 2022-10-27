package clazz.clazzloader;

/**
 * Created by JasonFitch on 12/26/2018.
 */
public class MainTest {

    static {
        System.out.println("MainTest static block");
    }

    private static String filed = "MainTest static filed";
}
