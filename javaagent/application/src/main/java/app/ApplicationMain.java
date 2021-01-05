package app;

import util.ClazzInfoUtils;

/**
 * Created by JasonFitch on 12/31/2020.
 */
public class ApplicationMain {

    public static void main(String[] args) {
        System.out.println("app.ApplicationMain.main");
        printClass();
    }

    private static void printClass() {
        ClazzInfoUtils.printClassInfo();
    }


}
