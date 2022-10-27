package basic.enumtest.using;

import basic.enumtest.EnumTest;

/**
 * Created by JasonFitch on 12/19/2018.
 */
public class UsingTest {

    public static void main(String[] args) {

        if (EnumTest.InnerEnum.APPLE == EnumTest.InnerEnum.APPLE) {
            System.out.println("import enumtest.EnumTest");
        }

        if (EnumTest.InnerEnum.APPLE == EnumTest.InnerEnum.APPLE) {
            System.out.println("import enumtest.EnumTest.InnerEnum");
        }

        if (EnumTest.InnerEnum.APPLE == EnumTest.InnerEnum.APPLE) {
            System.out.println("import static enumtest.EnumTest.InnerEnum.APPLE");
        }


        String apple = "APPLE";
        String orange = "ORANGE";
        switch (EnumTest.InnerEnum.getEnumByName(orange)) {
            case APPLE:
                System.out.println(EnumTest.InnerEnum.APPLE);
                break;
            case ORANGE:
                System.out.println(EnumTest.InnerEnum.ORANGE);
                break;
            default:
                System.out.println("DEFAULT");
        }

    }
}
