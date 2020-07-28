package bytecode.javassist;

import java.util.ArrayList;
import java.util.List;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.Descriptor;
import javassist.compiler.CompileError;
import javassist.compiler.MemberResolver;

/**
 * Created by JasonFitch on 7/28/2020.
 */
public class JavassistMemberResolverTest {

    public static void main(String[] args) throws NotFoundException, CompileError {

        System.out.println("############ MemberResolverTest ######################");
        MemberResolverTest();
        System.out.println();
    }

    public static void MemberResolverTest() throws NotFoundException, CompileError {
        ClassPool classPool = ClassPool.getDefault();

        CtClass ctClass = classPool.get("bytecode.material.SleeperParameterChild");
        String methodName = "sleep";

        List<String> argList = new ArrayList<>();
        argList.add("long[]");
        argList.add("java.lang.Long[]");
        argList.add("bytecode.material.MyLong[]");

        int nargs = argList.size();
        String[] cnames = new String[nargs];
        int[] dims = new int[nargs];
        int[] types = new int[nargs];

        for (int index = 0; index < argList.size(); ++index) {
            String argClassName = argList.get(index);
            //通过参数的完整 argClassName 来获取与之对应的参数类
            CtClass argCtClass = Descriptor.toCtClass(argClassName, classPool);
            //完整的参数类名为【long[]】
            System.out.println(argCtClass.getName());
            //参数的组件类名为【long】
            //只有数组类型的类才可以获取其组件类型
            //Exception in thread "main" java.lang.NullPointerException
            if (argCtClass.isArray()) {
                CtClass componentType = argCtClass.getComponentType();
                System.out.println(componentType.getName());
            }

            //使用参数的 argCtClass 对象生成完整的参数 descriptor
            String parameterDescriptor = Descriptor.of(argCtClass);
            //完整的参数描述符为 【[J】
            System.out.println(parameterDescriptor);

            //使用带数组维度前缀信息的参数 descriptor 来计算参数的数组维度数量
            //不像上面的方法 getComponentType，这个方法是基于 descriptor 的 String 来解析的，
            //他是更安全的，即使不是数组也不会像基于类结构的处理抛出异常，而是返回 arrayDimension 为 0 。
            //辅组类 Descriptor 中的很多对 descriptor 操作的方法都是处理字符串形式的 descriptor 的。
            int arrayDimension = Descriptor.arrayDimension(parameterDescriptor);
            dims[index] = arrayDimension;

            //通过去掉参数 descriptor 中的数组纬度前缀信息来提取真实的参数组件名,即数组中的每一个元素的类型
            String component = Descriptor.toArrayComponent(parameterDescriptor, arrayDimension);
            //参数的组件描述符为 【J】
            System.out.println(component);

            //将参数的组件名转化为对应的 java 类名，
            //其实他和 Descriptor.of 是互逆的，只不过前面已经去除了参数的数组信息了，所以这里转换的类名就是元素的类名了。
            String className = Descriptor.toClassName(component);
            cnames[index] = className;

            //从参数的组件名提取出对应的 java 类型，只需要组件的第一个字符便可以确定了。
            int type = MemberResolver.descToType(component.charAt(0));
            types[index] = type;

            System.out.println();
        }

        MemberResolver memberResolver = new MemberResolver(classPool);
        MemberResolver.Method lookupMethod = memberResolver.lookupMethod(ctClass, null, null, methodName, types, dims, cnames);
        System.out.println(lookupMethod);
        System.out.println(lookupMethod.info);
    }

}
