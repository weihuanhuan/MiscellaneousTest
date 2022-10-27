package clazz.bytecode.javassist;

import java.lang.reflect.Field;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.Loader;
import javassist.NotFoundException;
import javassist.bytecode.ConstPool;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;

/**
 * Created by JasonFitch on 8/3/2020.
 */
public class JavassistInitializationTest {

    private static String targetClassName = "bytecode.material.SleeperWithField";

    public static void main(String[] args) throws NotFoundException, CannotCompileException, IllegalAccessException, InstantiationException, ClassNotFoundException {

        System.out.println("########  clazzInitializationTest  ########");
        clazzInitializationTest();
        System.out.println();

        System.out.println("########  instanceInitializationTest  ########");
        instanceInitializationTest();
        System.out.println();

        System.out.println("########  finalInitializationTest  ########");
        finalInitializationTest();
        System.out.println();
    }

    private static void clazzInitializationTest() throws NotFoundException, CannotCompileException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        ClassPool classPool = ClassPool.getDefault();

        CtClass ctClass = classPool.get(targetClassName);
        if (ctClass.isFrozen()) {
            ctClass.defrost();
        }

        //查看反编译的字节码发现 getClassInitializer 对应的签名为【()V】
        //其执行过程中仅仅会初始化为 static 且非 final 修饰的 class 级别的域，
        //所以我们可以通过其来实现对 非final 的 class 级别域的修改。
        //"C:\Program Files\Java\jdk1.8.0_131\bin\javap.exe" -v -p bytecode.material.SleeperWithField
        CtConstructor classInitializer = ctClass.getClassInitializer();
        ExprEditor accessExprEditor = newFieldAccessExprEditor();
        classInitializer.instrument(accessExprEditor);

        Loader loader = new Loader(ClassLoader.getSystemClassLoader(), classPool);
        Class<?> aClass = loader.loadClass(targetClassName);
        Object instance = aClass.newInstance();
        printFieldInfo(instance);
    }

    private static void instanceInitializationTest() throws NotFoundException, ClassNotFoundException, IllegalAccessException, InstantiationException, CannotCompileException {
        ClassPool classPool = ClassPool.getDefault();

        CtClass ctClass = classPool.get(targetClassName);
        if (ctClass.isFrozen()) {
            ctClass.defrost();
        }

        //查看反编译的字节码发现 getDeclaredConstructors 对应的签名为【()V】
        //其执行过程中仅仅会初始化非 static 的 instance 级别的域，其中包括所有的 instance 级别的 final域，
        //所以我们可以通过其来实现对一个类的所有 非static 的 instance 级别的域的修改。
        //"C:\Program Files\Java\jdk1.8.0_131\bin\javap.exe" -v -p bytecode.material.SleeperWithField
        CtConstructor[] declaredConstructors = ctClass.getDeclaredConstructors();

        ExprEditor accessExprEditor = newFieldAccessExprEditor();
        for (CtConstructor declaredConstructor : declaredConstructors) {
            declaredConstructor.instrument(accessExprEditor);
        }

        Loader loader = new Loader(ClassLoader.getSystemClassLoader(), classPool);
        Class<?> aClass = loader.loadClass(targetClassName);
        Object instance = aClass.newInstance();
        printFieldInfo(instance);
    }

    private static void finalInitializationTest() throws NotFoundException, ClassNotFoundException, IllegalAccessException, InstantiationException {

        //结合先前的 instrument 处理 class initializer 和 class constructor 以及 class method 的可行性
        //我们可以完全控制一个类的 构造，方法，和域的处理，
        //但是唯独对于 static final field 的处理没有办法捕捉到，
        //如果只在本类中出现到还好说，但是 static final 的 field 会被内联到其他 class 文件中，因此要完全处理这种域需要跨 class file 进行处理。

    }

    private static ExprEditor newFieldAccessExprEditor() {
        ExprEditor exprEditor = new ExprEditor() {

            @Override
            public void edit(FieldAccess f) throws CannotCompileException {
                String fieldName = f.getFieldName();
                System.out.println(fieldName);

                CtBehavior where = f.where();
                System.out.println(where);
                String signature = where.getSignature();
                System.out.println(signature);

                f.replace(" $_ = $proceed(2000l); ");

                System.out.println();
            }
        };

        return exprEditor;
    }

    private static void printFieldInfo(Object instance) throws IllegalAccessException {
        System.out.println("-------- printFieldInfo --------");
        Class<?> aClass = instance.getClass();
        Field[] declaredFields = aClass.getDeclaredFields();

        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            String name = declaredField.getName();
            System.out.println(name);
            Object value = declaredField.get(instance);
            System.out.println(value);
        }
    }

}
