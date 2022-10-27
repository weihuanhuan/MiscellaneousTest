package clazz.InterfaceTest;

/**
 * Created by JasonFitch on 1/14/2019.
 */
public class InterfaceTest implements StaticInterface {


    static String name;

    static void setField(String String) {
        System.out.println("static impl:"+String);
    }

    @Override
    public void setPublic(String string) {
        name = string;
        System.out.println(name);
    }


}
