package StaticClass.clazz;

/**
 * Created by JasonFitch on 3/21/2019.
 */
public abstract class AbstractClassTest {

    public static void doSomething() {
        System.out.println("hello");
    }

    public static void main(String[] args) {

        AbstractClassTest.doSomething();

        //JF 抽象类不可实例化，但是其静态方法可以直接使用，
        //   因为抽象类可以被加载，解析，而静态方法是累级别的无需实例化便可以调用。
        //   new AbstractClassTest();
    }
}
