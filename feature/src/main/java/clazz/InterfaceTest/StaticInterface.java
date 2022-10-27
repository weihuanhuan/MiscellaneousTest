package clazz.InterfaceTest;

/**
 * Created by JasonFitch on 1/14/2019.
 */
public interface StaticInterface {

    static void setField(String String){
        System.out.println("static interface:"+String);
    };

    void setPublic(String string);
}
