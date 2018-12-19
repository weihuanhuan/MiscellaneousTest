package enumtest.using;

import enumtest.EnumTest;
import enumtest.EnumTest.E;

import static enumtest.EnumTest.E.TYPE;

/**
 * Created by JasonFitch on 12/19/2018.
 */
public class UsingTest {

    public static void main(String[] args) {

        if (EnumTest.E.TYPE == EnumTest.E.TYPE) {
            System.out.println("import enumtest.EnumTest");
        }

        if (E.TYPE == E.TYPE) {
            System.out.println("import enumtest.EnumTest.E");
        }

        if (TYPE == TYPE) {
            System.out.println("import static enumtest.EnumTest.E.TYPE");
        }

    }
}
