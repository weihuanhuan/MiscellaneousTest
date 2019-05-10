package reflect.InterfaceStatic;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by JasonFitch on 1/9/2019.
 */
public class InterfaceStaticTest {

    public static final String FACTORY_CLASS_NAME = "reflect.InterfaceStatic.LogFactoryImpl";

    public static void main(String[] args) {

        ClassLoader classLoader = null;
        Class<? extends LogFactory> clazz = null;
        try {
            classLoader = InterfaceStaticTest.class.getClassLoader();
            //JF 即使是实现的接口，在class表示类集合时，也可以用extends关键字，同时这里没有implements的用法
            //   Class<? super Object> s;       OK
            //   Class<? extends Object> e;     OK
            //   Class<? implements Object> i;  BAD
            clazz = (Class<? extends LogFactory>) classLoader.loadClass(FACTORY_CLASS_NAME);
            clazz.newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        try {
            assert clazz != null;

            Method getSingletonMethod = clazz.getMethod("getSingleton",null);
            //JF 反射调用静态方法，将第一个对象参数传递为null即可，如果方法没有参数，将参数列表参数也传递为null
            LogFactory logFactory = (LogFactory) getSingletonMethod.invoke(null, null);

            //JF 注意JDK1.5编译，即使无参数时，也不要传null作为参数，会报 违规的参数异常：参数数量错误
            //   而JDK1.8编译的代码没有此问题
            Method getSingletonMethod1 = clazz.getMethod("getSingleton");
            LogFactory logFactory1 = (LogFactory) getSingletonMethod1.invoke(null);

            assert  logFactory ==logFactory1;

            Logger reflectMethodInvokeLogger = logFactory.getLogInterface("ReflectMethodInvokeLogger");
            reflectMethodInvokeLogger.log(Level.INFO, "TestMessage");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }


    }

}
