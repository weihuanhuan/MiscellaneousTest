package clazzloader;

/**
 * Created by JasonFitch on 12/26/2018.
 */
public class ClassForNameTest {


    public static void main(String[] args) throws ClassNotFoundException {


        ClassLoader appClassloader = ClassLoader.getSystemClassLoader();

        //JF ext classloader 加载路径 -Djava.ext.dirs="F:/JetBrains/IntelliJ IDEA/BEStest/target"
        ClassLoader extClassLoader = appClassloader.getParent();

        //JF 不会初始化静态块
        extClassLoader.loadClass("clazzloader.MainTest");

        //JF 会初始化静态快
        Class.forName("clazzloader.MainTest", true, extClassLoader);

    }
}
