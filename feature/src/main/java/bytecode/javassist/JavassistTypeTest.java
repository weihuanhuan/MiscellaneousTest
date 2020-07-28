package bytecode.javassist;

import java.util.ArrayList;
import java.util.List;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtPrimitiveType;
import javassist.NotFoundException;
import javassist.bytecode.analysis.Type;

/**
 * Created by JasonFitch on 7/28/2020.
 */
public class JavassistTypeTest {

    public static void main(String[] args) throws NotFoundException, CannotCompileException {

        testJavassistType();

    }

    private static void testJavassistType() throws NotFoundException {
        ClassPool classPool = ClassPool.getDefault();

        List<String> argsList = new ArrayList<>();
        argsList.add("long[]");
        argsList.add("java.lang.Long[]");
        argsList.add("bytecode.material.MyLong[]");

        //true，父类，接口和子类之间可以
        CtClass ctClassChild = classPool.get("bytecode.material.SleeperParameterChild");
        Type typeChild = Type.get(ctClassChild);
        CtClass ctClass = classPool.get("bytecode.material.SleeperParameter");
        Type type = Type.get(ctClass);
        //其内部调用了 subtypeOf ，同时这个方法测试的是可赋值性，所以他还增加了两者对 null 情况的判断。
        //具体参考 javassist.bytecode.analysis.Type.UNINIT 的注释
        boolean assignableFrom = type.isAssignableFrom(typeChild);
        System.out.println(assignableFrom);

        //false，原始类和其对应的包装器类之间不可以
        CtClass ctClassLongWrapper = classPool.get("java.lang.Long");
        Type typeLongWrapper = Type.get(ctClassLongWrapper);

        //Javassist 对于原始对象的 CtClass 实例的获取参考下面两个类的域来处理
        //javassist.ClassPool.classes
        //javassist.CtClass.primitiveTypes
        CtClass ctClassLong = classPool.get("long");
        Type typeLong = Type.get(ctClassLong);
        boolean assignableFromLong = typeLong.isAssignableFrom(typeLongWrapper);
        System.out.println(assignableFromLong);

        //true，必须是继承关系才可以
        boolean subclassOf = ctClassChild.subclassOf(ctClass);
        System.out.println(subclassOf);
        //true，继承或者是实现关系都可以
        boolean subtypeOf = ctClassChild.subtypeOf(ctClass);
        System.out.println(subtypeOf);

        //false，原始类和其对应的包装器类之间不可以
        //对于包装器类，他可以赋值给其对应的原始类型是因为 JVM 对这个过程实现了自动拆箱和装箱，而实际上他们两着的类是没有关系的。
        boolean subtypeOfLong = ctClassLong.subtypeOf(ctClassLongWrapper);
        System.out.println(subtypeOfLong);


        //对于原始类型的类，
        //其不是 Class 的实例，不能调用 toClass 方法，其 ClassPool 对象为 null
        //Exception in thread "main" java.lang.NullPointerException
        //Class<?> toClass = ctClassLong.toClass();

        //而当我们向获取其对应的包装器类时，可以将其转化为 CtPrimitiveType 来处理。
        if (ctClassLong.isPrimitive()) {
            CtPrimitiveType ctPrimitiveType = (CtPrimitiveType) ctClassLong;
            String wrapperName = ctPrimitiveType.getWrapperName();
            System.out.println(wrapperName);
            CtClass wrapperNameCtClass = classPool.get(wrapperName);
            //jdk 不允许加载 java.lang 包下的类
            //Exception in thread "main" java.lang.SecurityException: Prohibited package name: java.lang
            //Class<?> wrapperNameToClass = wrapperNameCtClass.toClass();
        }
    }

}
