package util;

import java.lang.reflect.Field;
import lib.LibMain;

/**
 * Created by JasonFitch on 12/31/2020.
 */
public class ClazzInfoUtils {

    public static void printClassInfo() {
        System.out.println("agent util.ClazzInfoUtils.printClassInfo");
        Class<?> aClass = LibMain.class;
        System.out.println(aClass.getName());
        System.out.println(aClass.getClassLoader());

        Field[] declaredFields = aClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            System.out.println(declaredField.toString());
        }
        System.out.println();

    }

}
