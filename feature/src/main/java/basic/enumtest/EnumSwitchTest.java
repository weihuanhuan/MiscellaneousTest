package basic.enumtest;

/**
 * Created by JasonFitch on 12/19/2018.
 */
public class EnumSwitchTest {

    public static void main(String[] args) {

        EnumTest.InnerEnum innerEnum = null;

        //Exception in thread "main" java.lang.NullPointerException
        switch (innerEnum) {
            case APPLE:
            case ORANGE:
            case DUMMY:
            default:
        }
    }

}

