package InterfaceTest;

/**
 * Created by JasonFitch on 1/14/2019.
 */
public class Main {

    public static void main(String[] args) {
        StaticInterface interfaceTest = new InterfaceTest();
        InterfaceTest.setField("impl");
        StaticInterface.setField("interface");
        interfaceTest.setPublic("public");
    }

}
