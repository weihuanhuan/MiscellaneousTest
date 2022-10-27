package clazz.bytecode.javassist;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.Descriptor;

/**
 * Created by JasonFitch on 7/28/2020.
 */
public class JavassistDescriptorTest {

    public static void main(String[] args) throws NotFoundException, CannotCompileException {

        System.out.println("############ ClassNameToParameterDescriptorTest ######################");
        ClassNameToParameterDescriptorTest();
        System.out.println();

        System.out.println("############ ClassToParameterDescriptorTest ######################");
        ClassToParameterDescriptorTest();
        System.out.println();

        System.out.println("############ ParameterNameTest ######################");
        ParameterNameTest();
        System.out.println();

    }

    public static void ClassNameToParameterDescriptorTest() throws NotFoundException {
        ClassPool classPool = ClassPool.getDefault();

        List<String> argList = new ArrayList<>();
        argList.add("long[]");
        argList.add("java.lang.Long[]");
        argList.add("bytecode.material.MyLong[]");

        //附加参数给参数描述符之类的方法，需要被附加的对象本身是合法的描述符，所以这里默认是一个无参的参数描述符。
        String desc = "()";
        for (String argClassName : argList) {
            //使用 Descriptor 转化参数对应的 class name 为 CtClass ，其支持原始类型。
            CtClass ctClass = Descriptor.toCtClass(argClassName, classPool);
            System.out.println(ctClass.getName());
            desc = Descriptor.appendParameter(ctClass, desc);
        }
        System.out.println(desc);

        System.out.println("-----------------");

        String[] toArray = argList.toArray(new String[argList.size()]);
        //直接通过 ClassPool 来获取多个 class name 所对应的 CtClass 数组
        CtClass[] ctClasses = classPool.get(toArray);
        for (CtClass ctClass : ctClasses) {
            System.out.println(ctClass.getName());
        }
        //生成多个 CtClass 构成的参数的参数描述符。
        String ofParameters = Descriptor.ofParameters(ctClasses);
        System.out.println(ofParameters);

    }

    private static void ClassToParameterDescriptorTest() throws NotFoundException {
        ClassPool classPool = ClassPool.getDefault();

        CtClass ctClass = classPool.get("bytecode.material.SleeperParameterChild");
        printMethodSignature(ctClass, 2);

        System.out.println("-----------------");

        CtClass ctClassChild = classPool.get("bytecode.material.SleeperParameter");
        printMethodSignature(ctClassChild, 2);
    }

    private static void printMethodSignature(CtClass ctClass, int atLeastAmountOfParameter) throws NotFoundException {
        CtMethod[] methods = ctClass.getDeclaredMethods();
        for (CtMethod method : methods) {
            if (method.getParameterTypes().length < atLeastAmountOfParameter) {
                continue;
            }
            System.out.println();

            String signature = method.getSignature();
            //辅助类 Descriptor 可以从方法签名中取出返回值的部分，这样子就和我们上面手动构建的参数签名是一致的了。
            String paramDescriptor = Descriptor.getParamDescriptor(signature);
            System.out.println(paramDescriptor);

            //非泛型的方法是没有泛型签名的，此时 getGenericSignature 返回为 null
            String genericSignature = method.getGenericSignature();
            if (genericSignature == null) {
                continue;
            }
            //泛型方法的参数签名中依旧会包含泛型参数的参数名和边界信息。
            String genericParamDescriptor = Descriptor.getParamDescriptor(genericSignature);
            System.out.println(genericParamDescriptor);
        }

    }

    private static void ParameterNameTest() throws NotFoundException, CannotCompileException {
        ClassPool classPool = ClassPool.getDefault();

        CtClass ctClass = classPool.get("bytecode.material.SleeperParameterChild");

        //通过 JDK 原生的反社来获取，不过其结果是否是源码中的 parameterName ，依赖于 javac 的编译选项是否包含 -parameters
        Class<?> aClass = ctClass.toClass();
        for (Method method : aClass.getDeclaredMethods()) {
            for (Parameter parameter : method.getParameters()) {
                Class<?> parameterType = parameter.getType();
                System.out.println(parameterType.getName());
                String parameterName = parameter.getName();
                System.out.println(parameterName);

            }
        }

    }

}
