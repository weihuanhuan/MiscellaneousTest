package clazz.reflect;

/**
 * Created by JasonFitch on 5/13/2020.
 */
public class ReflectInterfaceTest {

    public static void main(String[] args) {

        ClazzB clazzB = new ClazzB();

        //itself
        //java.lang.Class.getInterfaces() 只会获取到 Class 直接实现的接口，即 implements 子句后面的接口类
        System.out.println("################# itself ###################");
        Class<?> clazz = clazzB.getClass();
        System.out.println(clazz);
        for (Class<?> aClass : clazz.getInterfaces()) {
            System.out.println(aClass);
        }

        //super class
        System.out.println("################# super class ###################");
        Class<?> superclass = clazzB.getClass().getSuperclass();
        System.out.println(superclass);
        for (Class<?> aClass : superclass.getInterfaces()) {
            System.out.println(aClass);
        }
    }
}
