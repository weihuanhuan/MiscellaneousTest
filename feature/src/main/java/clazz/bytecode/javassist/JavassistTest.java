package clazz.bytecode.javassist;

import clazz.bytecode.material.Sleeper;
import java.io.IOException;
import javassist.ByteArrayClassPath;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import javassist.NotFoundException;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.MethodInfo;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

/**
 * Created by JasonFitch on 7/16/2020.
 */
public class JavassistTest {

    private static String className = "bytecode.material.Sleeper";
    private static String methodName = "sleep";

    private static String callClassName = "java.lang.Thread";
    private static String callMethodName = "sleep";

    public static void main(String[] args) throws NotFoundException, CannotCompileException, IllegalAccessException, InstantiationException, InterruptedException {

        ClassPool classPool = ClassPool.getDefault();
        CtClass ctClass = classPool.get(className);

        //通过特定的类来 instrument，对类中的每个方法生效
        instrumentCtClass(ctClass);

        //通过特定的方法来 instrument，对类中特定的一个方法生效
        CtMethod ctMethod = ctClass.getDeclaredMethod(methodName);
        instrumentCtMethod(ctMethod);

        Class<?> newClass = ctClass.toClass();
        Object instance = newClass.newInstance();
        if (instance instanceof Sleeper) {
            Sleeper sleeper = (Sleeper) instance;
            sleeper.sleep();
            sleeper.anotherSleep();
        }

    }

    public byte[] doInstrumentSystemPath(String className, byte[] classfileBuffer) throws NotFoundException, CannotCompileException, IOException {
        //补充 系统类路径 作为加载路径
        //javassist.CannotCompileException: [source error] no such class: java.lang.Thread
        ClassPool classPool = new ClassPool();
        classPool.appendSystemPath();

        //将原始的 class 的 byte code 定义为 新的 class
        ByteArrayClassPath byteArrayClassPath = new ByteArrayClassPath(className, classfileBuffer);
        classPool.insertClassPath(byteArrayClassPath);

        CtClass ctClass = classPool.get(className);
        CtMethod ctMethod = ctClass.getDeclaredMethod(methodName);
        instrumentCtMethod(ctMethod);

        byte[] bytes = ctClass.toBytecode();
        return bytes;
    }

    public byte[] doInstrumentLoaderPath(ClassLoader loader, String className, byte[] classfileBuffer) throws NotFoundException, CannotCompileException, IOException {
        //补充特定的 classloader 作为加载路径
        //Caused by: compile error: no such class: System.out
        ClassPool classPool = new ClassPool();
        LoaderClassPath loaderClassPath = new LoaderClassPath(loader);
        classPool.appendClassPath(loaderClassPath);

        ByteArrayClassPath byteArrayClassPath = new ByteArrayClassPath(className, classfileBuffer);
        classPool.insertClassPath(byteArrayClassPath);

        CtClass ctClass = classPool.get(className);
        instrumentCtClass(ctClass);

        byte[] bytes = ctClass.toBytecode();
        return bytes;
    }

    private static void instrumentCtMethod(CtMethod ctMethod) throws CannotCompileException {
        ExprEditor exprEditor = getThreadSleepReplaceExprEditor();
        ctMethod.instrument(exprEditor);
    }

    private static void instrumentCtClass(CtClass ctClass) throws CannotCompileException {
        ExprEditor exprEditor = getThreadSleepAroundExprEditor();
        ctClass.instrument(exprEditor);
    }

    private static ExprEditor getThreadSleepReplaceExprEditor() {
        ExprEditor exprEditor = new ExprEditor() {
            public void edit(MethodCall m) throws CannotCompileException {
                if (m.getClassName().equals(callClassName) && m.getMethodName().equals(callMethodName)) {
                    System.out.println(m.where());
                    System.out.println("lineNumber: " + m.getLineNumber());
                    m.replace("$_ = $proceed(500l);");
                }
            }
        };
        return exprEditor;
    }

    private static ExprEditor getThreadSleepAroundExprEditor() {
        ExprEditor exprEditor = new ExprEditor() {
            public void edit(MethodCall m) throws CannotCompileException {
                if (m.getClassName().equals(callClassName) && m.getMethodName().equals(callMethodName)) {
                    CtBehavior ctBehavior = m.where();
                    System.out.println(ctBehavior);
                    System.out.println("lineNumber: " + m.getLineNumber());
                    ctBehavior.insertBefore("System.out.println(\"enter\");");
                    ctBehavior.insertAfter("System.out.println(\"exit\");");
                }
            }
        };
        return exprEditor;
    }

    private static void getConstantPool(CtClass ctClass) {
        ClassFile classFile = ctClass.getClassFile();
        ConstPool classFileConstPool = classFile.getConstPool();

        MethodInfo methodInfo = classFile.getMethod(methodName);
        ConstPool methodInfoConstPool = methodInfo.getConstPool();
    }

}
