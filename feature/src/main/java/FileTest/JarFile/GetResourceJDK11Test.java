package FileTest.JarFile;

/**
 * Created by JasonFitch on 3/22/2021.
 */
public class GetResourceJDK11Test {

    public static void main(String[] args) throws java.net.MalformedURLException, ClassNotFoundException {
        System.out.println("################### classGetResources ###################");
        classGetResources();
    }

    private static void classGetResources() throws java.net.MalformedURLException {

        String configFile = "/log4j2.xml";

        java.net.URL classResource1 = GetResourceJDK11Test.class.getResource(configFile);
        System.out.println("url:" + classResource1);
        System.out.println("class:" + GetResourceJDK11Test.class);
        System.out.println("classloader:" + GetResourceJDK11Test.class.getClassLoader());

        java.net.URL classResource2 = GetResourceJDK11Test.class.getClass().getResource(configFile);
        System.out.println("url:" + classResource2);
        System.out.println("class:" + GetResourceJDK11Test.class.getClass());
        System.out.println("classloader:" + GetResourceJDK11Test.class.getClass().getClassLoader());

        //这里要注意 .class 和 .class.getClass() 的区别，
        // .class 返回表示当前类的类对象实例，是静态的，这里是一个表示 GetResourceJDK11Test 类实例
        // .class.getClass() 在 GetResourceJDK11Test 类实例的基础上，调用了 java.lang.Class 对象的 getClass 方法，
        //                   此时返回的是 java.lang.Class 类的实例
        // 而 java.lang.Class 属于 jdk 的类，GetResourceJDK11Test 属于用户类。
        // 所以 .class 使用的类加载器就是 GetResourceJDK11Test 的类加载器，这个是 system app classloader
        // 而 .class.getClass() 则使用了 jdk 的 bootstrap classloader

        // 因此定位 log4j2.xml 资源时，由于 class 的 getResource() 方法都是委托给其 classloader 来完成的。
        // 这里是  system app classloader 所以对于 .class 的形式来说，其可以获取到应用的 resources 下面的文件。

        // 但是对于 .class.getClass的形式来说，这里虽然 classloader 为 null
        // 他在 jdk8 中，当 classloader 为 null，则会转而使用 system app classloader 来进行加载资源，
        // 此时会遍历 java.net.URLClassLoader.ucp，即 class path 来查找资源

        // 但是在 jdk11 中由于 jdk9 引入的模块化特性，在加上 java.lang.Class 类属于 java.base 模块，
        // 所以这里直接使用 java.base 模块来加载资源，
        // 但是 java.base 模块是不会对 class path 中的资源进行查找，故无法找见。

        //----------jdk11----------
//        class:class FileTest.JarFile.GetResourceJDK11Test
//        classloader:jdk.internal.loader.ClassLoaders$AppClassLoader@1f89ab83
//        url:null
//        class:class java.lang.Class
//        classloader:null

//        java.net.URL classResource2 = GetResourceJDK11Test.class.getClass().getResource(configFile);
//        "main@1" prio=5 tid=0x1 nid=NA runnable
//        java.lang.Thread.State: RUNNABLE
//        at jdk.internal.loader.BuiltinClassLoader.findResource(BuiltinClassLoader.java:439)
//        at jdk.internal.loader.BuiltinClassLoader.findResource(BuiltinClassLoader.java:225)
//        at jdk.internal.loader.BootLoader.findResource(BootLoader.java:135)
//        at java.lang.Class.getResource(Class.java:2726)
//        at FileTest.JarFile.GetResourceJDK11Test.classGetResources(GetResourceJDK11Test.java:21)
//        at FileTest.JarFile.GetResourceJDK11Test.main(GetResourceJDK11Test.java:10)

        //----------jdk8----------
//        class:class FileTest.JarFile.GetResourceJDK11Test
//        classloader:sun.misc.Launcher$AppClassLoader@18b4aac2
//        url:file:/F:/JetBrains/IntelliJ%20IDEA/MiscellaneousTest/feature/target/classes/log4j2.xml
//        class:class java.lang.Class
//        classloader:null

//        java.net.URL classResource2 = GetResourceJDK11Test.class.getClass().getResource(configFile);
//        "main@1" prio=5 tid=0x1 nid=NA runnable
//        java.lang.Thread.State: RUNNABLE
//        at sun.misc.URLClassPath.findResource(URLClassPath.java:213)
//        at java.net.URLClassLoader$2.run(URLClassLoader.java:569)
//        at java.net.URLClassLoader$2.run(URLClassLoader.java:567)
//        at java.security.AccessController.doPrivileged(AccessController.java:-1)
//        at java.net.URLClassLoader.findResource(URLClassLoader.java:566)
//        at java.lang.ClassLoader.getResource(ClassLoader.java:1096)
//        at java.lang.ClassLoader.getSystemResource(ClassLoader.java:1226)
//        at java.lang.Class.getResource(Class.java:2265)
//        at FileTest.JarFile.GetResourceJDK11Test.classGetResources(GetResourceJDK11Test.java:21)
//        at FileTest.JarFile.GetResourceJDK11Test.main(GetResourceJDK11Test.java:10)


    }


}
