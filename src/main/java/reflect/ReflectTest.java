package reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

/**
 * Created by JasonFitch on 8/24/2018.
 */
public class ReflectTest
{
    public static void main(String[] args)
    {
        try
        {
            Class[] classes = SupClass.class.getDeclaredClasses();
            System.out.println(Arrays.deepToString(classes));
            System.out.println("------------------classes--------------------");
            for (Class clazz : classes)
            {
                Field[] fields = clazz.getDeclaredFields();
                System.out.println(Arrays.deepToString(fields));
                System.out.println("-------------------fields-------------------");
                for (Field field : fields)
                {
                    Object instance = null;
//                    instance = clazz.newInstance();
                    //这里无法使用 newInstacne() 实例化这个类，会抛出异常
//java.lang.IllegalAccessException: Class reflect.ReflectTest can not access a member of class reflect.SupClass$InnerClass with modifiers "private"
//    at sun.reflect.Reflection.ensureMemberAccess(Reflection.java:102)
//    at java.lang.Class.newInstance(Class.java:436)
//因为 java.lang.Class.newInstance() 方法相当于调用 new ClassXXX() ，即类的无参构造器，而反射出来的该类是一个私有的类,其无参构造器默认也是私有的，
// 如，手动创建一个 public 的构造器,则调用 newInstance()就没有问题。

                    Constructor[] constructors = clazz.getDeclaredConstructors();
                    System.out.println(Arrays.deepToString(constructors));
                    System.out.println("---------------------contructors-------------------");
                    for (Constructor constructor : constructors)
                    {
                        try
                        {
                            constructor.setAccessible(true);
                            instance = constructor.newInstance();
                            System.out.println(instance);
                            System.out.println("----------------------instance--------------------");
                        } catch (InvocationTargetException e)
                        {
                            e.printStackTrace();
                        }
                    }

                    field.setAccessible(true);
                    Object value = field.get(instance);
                    System.out.println(value);
                }
            }
        } catch (InstantiationException e)
        {
            e.printStackTrace();
        } catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }
}

class SupClass
{
    private String outter = "outterValue";

    private static class InnerClass
    {
//        public InnerClass()
//        {
//            System.out.println("------------------Create InnerClass---------------");
//        }

        private String innerField = "innerValue";
    }
}
