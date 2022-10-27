package clazz.reflect;

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
//                    这里无法使用 newInstacne() 实例化这个类，会抛出异常
//java.lang.IllegalAccessException: Class reflect.ReflectTest can not access a member of class reflect.SupClass$InnerClass with modifiers "private"
//    at sun.reflect.Reflection.ensureMemberAccess(Reflection.java:102)
//    at java.lang.Class.newInstance(Class.java:436)
//因为 java.lang.Class.newInstance() 方法相当于调用 new ClassXXX() ，即类的无参构造器，而反射出来的该类是一个私有的类,其无参构造器默认也是私有的，那么便不能 new 私有构造器了。
// 如，手动创建一个 public 的无参构造器,则调用 newInstance()就没有问题。
// 但是注意如果创建了，其他的非无参构造器，那么系统会认为已经有了构造器，便不会自行创建默认的无参构造器了，会导致这个调用产生，如下异常：
//                    java.lang.InstantiationException: reflect.SupClass$InnerClass
//                    at java.lang.Class.newInstance(Class.java:427)
//                    at reflect.ReflectTest.main(ReflectTest.java:28)
//                    Caused by: java.lang.NoSuchMethodException: reflect.SupClass$InnerClass.<init>()
//                    at java.lang.Class.getConstructor0(Class.java:3082)
//                    at java.lang.Class.newInstance(Class.java:412)

                    Constructor[] constructors = clazz.getDeclaredConstructors();
                    System.out.println(Arrays.deepToString(constructors));
                    System.out.println("---------------------contructors-------------------");
                    for (Constructor constructor : constructors)
                    {
                        try
                        {
                            constructor.setAccessible(true);
                            instance = constructor.newInstance();   //有可变数量的参数，这里没写而已。
//                            clazz.newInstance();   //一定是无参的。
//                          注意构造器的 java.lang.reflect.Constructor.newInstance() 方法是有【Object ... 】类型的参数的，
//                          可以传递初始化参数的，调用时必须要匹配参数。而，java.lang.Class.newInstance() 方法不可以传递参数，只能调用无参构造函数
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

                clazz.getInterfaces();
                //按照顺序返回这个类实现的接口
                clazz.getDeclaredClasses();
                //返回这个类内部声明的所有访问权限的 类，接口，除了那些继承来的类和接口.

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
