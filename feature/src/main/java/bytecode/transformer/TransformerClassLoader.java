package bytecode.transformer;

import java.io.InputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JasonFitch on 7/16/2020.
 */
public class TransformerClassLoader extends URLClassLoader {

    private static String targetClassName = "bytecode.material.Sleeper";

    private List<ClassFileTransformer> classFileTransformers = new ArrayList<>();

    public TransformerClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public void addClassFileTransformer(ClassFileTransformer classFileTransformer) {
        if (classFileTransformer == null) {
            return;
        }
        classFileTransformers.add(classFileTransformer);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        // 打破双亲委派模型，特定的类直接使用自己内部的 findClass 方法
        // 原因是这个 demo 中， TransformerClassLoader 的加载路径和 SystemAppClassloader 是相同的
        // 所以必须打破默认的 双亲委派模型，使其跳过查找 加载过的缓存 和 parent的路径， 来让自己加载这个特定的类。
        // 注意类加载时的多线程问题。
        synchronized (getClassLoadingLock(name)) {
            if (name.equals(targetClassName)) {
                return findClassInternal(name);
            } else {
                return super.loadClass(name, resolve);
            }
        }
    }

    @Override
    protected Class<?> findClass(String className) throws ClassNotFoundException {
        // 找个方法其实是不比重写的，因为上面的 loadClass 重写已经将特定的 class 转变为 Class 对象使用了。
        // 这里的目的是为了说明 JDK 本身是在 findClass 中进行 class文件的加载，
        // 然后使用 class文件的 bytes 通过 defineClass 转变为真正的 class 对象。
        // 也就是直接修改这个 bytes 也可以在 Class 对象产生前，修改类的结构，即像 javassist/asm 做的那样。
        if (className.equals(targetClassName)) {
            return findClassInternal(className);
        }
        return super.findClass(className);
    }

    private Class<?> findClassInternal(String className) throws ClassNotFoundException {
        try {
            // 不要加载原始的 class 因为这个类也在 parent 的 类路径中，加载后就不能在 transform 了
            // java.lang.RuntimeException: bytecode.material.Sleeper class is frozen
            // 参考 jdk 接口 java.lang.instrument.ClassFileTransformer 的相关文档
            //byte[] bytecode = javassistTest.getBytecode(name);

            // 所以，我们应该使用 getResource 来获取原始类的 bytecode 信息，供 transform 失败时 fallback 到原始的类。
            String resourceName = targetClassName.replace(".", "/") + ".class";
            InputStream resourceAsStream = this.getResourceAsStream(resourceName);
            byte[] oldByteCode = new byte[resourceAsStream.available()];
            resourceAsStream.read(oldByteCode);

            byte[] newByteCode = invokeTransformer(this, className, null, null, oldByteCode);
            return defineClass(className, newByteCode, 0, newByteCode.length);
        } catch (Exception e) {
            throw new ClassNotFoundException(e.getMessage(), e);
        }
    }

    private byte[] invokeTransformer(TransformerClassLoader transformerClassLoader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (classFileTransformers.isEmpty()) {
            return classfileBuffer;
        }

        //处理多个 ClassFileTransformer
        byte[] resultByteCode = classfileBuffer;
        for (ClassFileTransformer transformer : classFileTransformers) {
            byte[] newByteCode = transformer.transform(transformerClassLoader, className, classBeingRedefined, protectionDomain, resultByteCode);
            if (newByteCode != null) {
                resultByteCode = newByteCode;
            }
        }
        return resultByteCode;
    }

}
