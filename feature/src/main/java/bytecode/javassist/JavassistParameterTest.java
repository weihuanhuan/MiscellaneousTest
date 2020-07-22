package bytecode.javassist;

import bytecode.material.SleeperParameter;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
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

        ClassPool classPool = ClassPool.getDefault();
        CtClass ctClass = classPool.get(className);

        Class<?> aClass = ctClass.toClass();

        SleeperParameter<Integer> integerSleeperParameter = new SleeperParameter<>();
        Class<? extends SleeperParameter> aClass1 = integerSleeperParameter.getClass();

        System.out.println();
    }

}
