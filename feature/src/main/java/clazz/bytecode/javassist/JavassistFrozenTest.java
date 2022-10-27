package clazz.bytecode.javassist;

import clazz.bytecode.material.Sleeper;
import clazz.bytecode.transformer.TransformerClassLoader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

/**
 * Created by JasonFitch on 7/16/2020.
 */
public class JavassistFrozenTest {

    private static String className = "bytecode.material.Sleeper";
    private static String methodName = "sleep";
    private static String anotherMethodName = "anotherSleep";

    private static String calleeClassName = "java.lang.Thread";
    private static String calleeMethodName = "sleep";

    public static void main(String[] args) throws NotFoundException, CannotCompileException, IllegalAccessException, InstantiationException, InterruptedException, IOException, InvocationTargetException {

        ClassPool classPool = ClassPool.getDefault();
        CtClass ctClass = classPool.get(className);

        System.out.println("---------------- instrumentCtClass -----------------");

        //对于 CtClass 来说其 instrument 会间接调用其实现的 javassist.CtClassType.checkModify() 方法将类标记为修改了的
        //这里的标记是无视 instrument 的过程中 javassist.expr.ExprEditor 是否是真正的修改了源码的事实的，
        //所以对于类来说要判断他的 bytecode 是否被修改这个方法是不科学的，
        //甚至是 ctClass.getClassFile(); 方法的调用也会标记为 true.
        instrumentCtClass(ctClass);
        boolean modifiedClass = ctClass.isModified();
        System.out.println(modifiedClass);

        //当一个 ClassPool 中的类执行了解析 class 对应的 bytecode 行为后其就会被标记为 frozen ，之后便不能在对他的 bytecode 进行修改了。
        //注意 ctClass.writeFile() 和 ctClass.toClass() 等需要 bytecode 方法的内部实现都会调用 ctClass.toBytecode();
        //Exception in thread "main" java.lang.RuntimeException: bytecode.material.Sleeper class is frozen
        ctClass.toBytecode();
//        ctClass.writeFile();

        //对于一个 classloader 来说，一个 class 他只能加载一次，相对于 toBytecode 和 writeFile 来说，他们只是需要 bytecode 信息，只要读取 class 文件的字节流就可以了
        //而 toClass 方法由于要构建真实的 java.lang.Class 对象，所以他会真正的触发类加载的加载行为，
        //所以对于同一个 classloader 来说，即使我们把 bytecode 进行了 defrost ，
        //也不能在调用了 toClass 之后，二次对其 bytecode 修改，然后使用修改后的 bytecode 去加载二次修改的类了。这时会出现如下 Error。
        //当然如果我们只是需要二次修改其 bytecode ，然后将其交给其他的 classloader 来加载，那么还是可以对改 CtClass 执行 instrument 的，
        //或者也可以实现一个可重载的 classloader 来使用同一个 classloader 进行加载先前已经加载了的类。
        //Caused by: java.lang.ClassFormatError: loader (instance of  sun/misc/Launcher$AppClassLoader): attempted  duplicate class definition for name: "bytecode/material/Sleeper"
        Class<?> firstModClass = ctClass.toClass();

        System.out.println("---------------- instrumentCtMethod -----------------");

        //当然我们可以使用 defrost 这个方法来解除 frozen 状态，
        //当然这要求这个类 将要被重载 或者是 需要再次写出 其 bytecode 的情况下才可以。
        ctClass.defrost();

        //而对于 CtMethod 来说，如果 ExprEditor 没有执行真实的修改 bytecode 操作其不会将 CtClass 的状态标记为修改的。
        //但是要注意这类 XXXInfo 方法，如 ctMethod.getMethodInfo();
        //是会间接通过 javassist.CtClassType.checkModify() 来标记其状态为修改了的，即使你获得他之后什么也不干
        //也就是 javassist 在很多情况下认为可能修改 bytecode 的接口的使用就会修改，并标记其状态。
        CtMethod ctMethod = ctClass.getDeclaredMethod(methodName);
        instrumentCtMethod(ctMethod);

        boolean modifiedCtMethod = ctClass.isModified();
        System.out.println(modifiedCtMethod);

        //由于先前已经使用了 Launcher$AppClassLoader 加载了 CtClass 对象，
        //所以我们要加载二次修改的就只能使用另外一个 classloader 来对其进加载了。
        //CtClass.toClass() 在不指定 classloader 时，其内部默认使用 ContextClassLoader 来加载 CtClass 为真实的 java.lang.Class。
        ClassLoader transformerClassLoader = getClassloader();
        Thread.currentThread().setContextClassLoader(transformerClassLoader);
        Class<?> secondModClass = ctClass.toClass();

        System.out.println();
        System.out.println("---------------- firstModClass -----------------");
        printClassInfo(firstModClass);

        //一个被所引用的类的 隐式加载 ，由其引用类的 classloader 来加载，
        //而显示的加载就是直接指定使用某个 classloader 的形式，比如 java.lang.ClassLoader.loadClass() 这个方法。
        //所以这里 secondModClass 的 classloader 和 main 类的隐式触发的 Sleeper 类不是同一个类加载器，即他们不是同一个类。
        System.out.println();
        System.out.println("---------------- secondModClass -----------------");
        printClassInfo(secondModClass);

        System.out.println();
        System.out.println("---------------- implicitClassLoader Info  -----------------");
        ClassLoader implicitClassLoader = Sleeper.class.getClassLoader();
        System.out.println(implicitClassLoader);
    }

    private static ClassLoader getClassloader() {
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        URL[] systemUrls = ((URLClassLoader) systemClassLoader).getURLs();
        TransformerClassLoader transformerClassLoader = new TransformerClassLoader(systemUrls, systemClassLoader);
        return transformerClassLoader;
    }

    private static void instrumentCtClass(CtClass ctClass) throws CannotCompileException {
        ExprEditor exprEditor = getThreadSleepSetBodyExprEditor();
        ctClass.instrument(exprEditor);
    }

    private static void instrumentCtMethod(CtMethod ctMethod) throws CannotCompileException {
        ExprEditor exprEditor = getThreadSleepReplaceExprEditor();
        ctMethod.instrument(exprEditor);
    }

    private static ExprEditor getThreadSleepSetBodyExprEditor() {
        ExprEditor exprEditor = new ExprEditor() {
            public void edit(MethodCall m) throws CannotCompileException {
                if (m.getClassName().equals(calleeClassName) && m.getMethodName().equals(calleeMethodName)) {
                    CtBehavior ctBehavior = m.where();
                    if (!ctBehavior.getName().equals(anotherMethodName)) {
                        return;
                    }

                    System.out.println(ctBehavior);
                    //null 可以清空一个method，javassist 会使用默认的方法实现来代替，其仅仅包含最简单的 return 语句。
//                    ctBehavior.setBody(null);
                    ctBehavior.setBody("{System.out.println(\"anotherSleep set_body\");}");
                }
            }
        };
        return exprEditor;
    }

    private static ExprEditor getThreadSleepReplaceExprEditor() {
        ExprEditor exprEditor = new ExprEditor() {
            public void edit(MethodCall m) throws CannotCompileException {
                if (m.getClassName().equals(calleeClassName) && m.getMethodName().equals(calleeMethodName)) {
                    CtBehavior ctBehavior = m.where();
                    System.out.println(ctBehavior);
                    //对于 method call 来说其修改状态的检测是在 replace 中处理的，所以不调用这个方法就不会修改状态。
                    m.replace("$_ = $proceed(500l);");
                }
            }
        };
        return exprEditor;
    }

    private static void printClassInfo(Class<?> modClass) throws IllegalAccessException, InstantiationException, InterruptedException, InvocationTargetException {
        ClassLoader classLoader = modClass.getClassLoader();
        System.out.println(classLoader);

        Object instance = modClass.newInstance();
        if (instance instanceof Sleeper) {
            System.out.println("same classloader!");
            Sleeper sleeper = (Sleeper) instance;
            sleeper.sleep();
            sleeper.anotherSleep();
        } else {
            System.out.println("different classloader!");
            for (Method method : modClass.getDeclaredMethods()) {
                method.invoke(instance,null);
            }

        }
    }


}
