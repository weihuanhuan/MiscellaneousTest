package bytecode.javassist;

import bytecode.material.MyChildInteger;
import bytecode.material.SleeperParameter;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.Loader;
import javassist.NotFoundException;

/**
 * Created by JasonFitch on 7/22/2020.
 */
public class JavassistParameterTest {

    private static String className = "bytecode.material.SleeperParameter";
    private static String methodName = "sleep";
    private static String anotherMethodName = "anotherSleep";

    private static String calleeClassName = "java.lang.Thread";
    private static String calleeMethodName = "sleep";

    public static void main(String[] args) throws NotFoundException, CannotCompileException {

        javassistConstructorTest();

        manualTypeTokenTest();

        googleTypeTokenTest();

        System.out.println();
    }

    private static void javassistConstructorTest() throws NotFoundException, CannotCompileException {
        System.out.println();
        System.out.println("------------ javassistConstructorTest ----------------");


        ClassPool classPool = ClassPool.getDefault();
        CtClass ctClass = classPool.get(className);

        for (CtConstructor ctConstructor : ctClass.getConstructors()) {
            //插入本类的任何 super 类的构造方法之前
            ctConstructor.insertBefore("{}");
            //插入本类的任何 super 类的构造方法之后
            ctConstructor.insertBeforeBody("{}");

            ctConstructor.insertAfter("{}");
            ctConstructor.setBody("{}");
        }

        //Javassist 工具类，防止只使用 Launcher$AppClassLoader 加载一个类多次产生的重复加载问题。
        // This is a sample class loader using ClassPool Unlike a regular class loader,
        // this class loader obtains bytecode from a ClassPool
        Loader loader = new Loader(classPool);
        Class<?> toClass = ctClass.toClass(loader, null);

        String toClassName = toClass.getName();
        System.out.println(toClassName);
        ClassLoader classLoader = toClass.getClassLoader();
        System.out.println(classLoader);
    }

    private static void manualTypeTokenTest() {
        System.out.println();
        System.out.println("------------ manualTypeTokenTest ----------------");

        //创建一个泛型类的子类实例，这里采用的匿名类的形式来创建的
        SleeperParameter integerSleeperParameter = new SleeperParameter<MyChildInteger>() {
        };
        Class<? extends SleeperParameter> integerSleeperParameterClass = integerSleeperParameter.getClass();
        System.out.println(integerSleeperParameterClass.getName());

        //获取其泛型父类的信息
        //注意如果直接使用 java.lang.Class.getSuperclass() 的话，此时拿到的 super class 已经是类型擦除后的类了，无法通过他来审视泛型信息。
        //integerSleeperParameterClass.getSuperclass();
        System.out.println("---- generic type ----");
        Type genericSuperclass = integerSleeperParameterClass.getGenericSuperclass();
        String typeName = genericSuperclass.getTypeName();
        System.out.println(typeName);

        //父类为泛型类时，对其父类所持有的泛型信息进行解析
        if (genericSuperclass instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;

            //从泛型的类中取得其全部的泛型参数的真实类型
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();

            //从泛型的类中取得其原始的非泛型类型
            Type rawType = parameterizedType.getRawType();
            String rawTypeName = rawType.getTypeName();
            System.out.println(rawTypeName);

            //从泛型的类中取得其全部的泛型参数的参数名
            TypeVariable[] typeParameters = null;
            if (rawType instanceof Class) {
                Class rawTypeClass = (Class) rawType;
                typeParameters = rawTypeClass.getTypeParameters();
            }

            //对于一个泛型的类，其泛型参数的真实类型和泛型参数的参数名是按照顺序对应的
            System.out.println("---- generic parameter ----");
            for (int i = 0; i < actualTypeArguments.length; ++i) {
                //泛型参数的名字
                TypeVariable typeParameter = typeParameters[i];
                String typeParameterName = typeParameter.getName();
                System.out.println(typeParameterName);

                //泛型参数的上界信息
                for (Type type : typeParameter.getBounds()) {
                    System.out.println(type.getTypeName());
                }

                //泛型参数的真实类型
                Type actualTypeArgument = actualTypeArguments[i];
                String actualTypeArgumentTypeName = actualTypeArgument.getTypeName();
                System.out.println(actualTypeArgumentTypeName);
            }
        }
    }

    private static void googleTypeTokenTest() {
        System.out.println();
        System.out.println("------------ googleTypeTokenTest ----------------");

        //google 的 gson 中的工具类 TypeToken,
        // Represents a generic type T. Java doesn't yet provide a way to represent generic types, so this class does.
        // Forces clients to create a subclass of this class which enables retrieval the type information even at runtime.
        //
        //其内部通过如下的 jdk api 来获取泛型的真实类型信息
        // java.lang.Class.getGenericSuperclass
        // java.lang.reflect.ParameterizedType.getActualTypeArguments
        TypeToken typeToken = new TypeToken<SleeperParameter<MyChildInteger>>() {
        };

        Type type = typeToken.getType();
        String typeName = type.getTypeName();
        System.out.println(typeName);

        Class rawType = typeToken.getRawType();
        String rawTypeName = rawType.getName();
        System.out.println(rawTypeName);
    }

}
