package FileTest.JarFile;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Created by JasonFitch on 3/13/2020.
 */
public class GetResourceTest {

    public static void main(String[] args) throws MalformedURLException, ClassNotFoundException {

        String userDir = System.getProperty("user.dir");
        System.out.println(userDir);

        String path = userDir+"/springmvc/target/springmvc.war";
        System.out.println(path);

        File file = new File(path);
        if (!file.exists() || !file.isFile()) {
            System.out.println("error");
            return;
        }

        System.out.println("################### classloaderGetResources ###################");
        classloaderGetResources(file);

        System.out.println("################### classGetResources ###################");
        classGetResources(file);

    }

    public static void classloaderGetResources(File file) throws MalformedURLException, ClassNotFoundException {
        URL url = file.toURI().toURL();
        URL[] urls = new URL[]{url};

        URLClassLoader urlClassLoader = new URLClassLoader(urls, ClassLoader.getSystemClassLoader());
        for (URL url1 : urlClassLoader.getURLs()) {
            System.out.println(url1);
        }

        System.out.println("-----------------------");
        //JF 因为classloader底层使用 sun.misc.URLClassPath.JarLoader 间接通过 JarFile.getJarEntry() 来获取资源的。
        //   所以和jar包中的路径原则相同，不能以 /(根路径) 开始来查找，否则直接返回 null
        URL loadResource1 = urlClassLoader.getResource("");
        System.out.println(loadResource1);

        URL loadResource2 = urlClassLoader.getResource("/");
        System.out.println(loadResource2);

        URL loadResource3 = urlClassLoader.getResource("/writerflushclose.jsp");
        System.out.println(loadResource3);

        URL loadResource4 = urlClassLoader.getResource("writerflushclose.jsp");
        System.out.println(loadResource4);

//        "main@1" prio=5 tid=0x1 nid=NA runnable
//        java.lang.Thread.State: RUNNABLE
//        at java.util.zip.ZipCoder.getBytes(ZipCoder.java:77)
//        at java.util.zip.ZipFile.getEntry(ZipFile.java:316)
//        - locked <0x208> (a java.util.jar.JarFile)
//        at java.util.jar.JarFile.getEntry(JarFile.java:240)
//        at java.util.jar.JarFile.getJarEntry(JarFile.java:223)
//        at sun.misc.URLClassPath$JarLoader.getResource(URLClassPath.java:1054)
//        at sun.misc.URLClassPath$JarLoader.findResource(URLClassPath.java:1032)
//        at sun.misc.URLClassPath.findResource(URLClassPath.java:225)
//        at java.net.URLClassLoader$2.run(URLClassLoader.java:572)
//        at java.net.URLClassLoader$2.run(URLClassLoader.java:570)
//        at java.security.AccessController.doPrivileged(AccessController.java:-1)
//        at java.net.URLClassLoader.findResource(URLClassLoader.java:569)
//        at java.lang.ClassLoader.getResource(ClassLoader.java:1096)
//        at FileTest.JarFile.JarFileListTest.classloaderGetResources(JarFileListTest.java:81)
//        at FileTest.JarFile.JarFileListTest.main(JarFileListTest.java:32)
    }


    private static void classGetResources(File file) throws MalformedURLException {
        URL url = file.toURI().toURL();
        URL[] urls = new URL[]{url};

        URLClassLoader urlClassLoader = new URLClassLoader(urls, ClassLoader.getSystemClassLoader());
        for (URL url1 : urlClassLoader.getURLs()) {
            System.out.println(url1);
        }

        System.out.println("-----------------------");
        //JF Class.getResource 相比于 ClassLoader.getResource 会先调用 Class.resolveName() 来处理资源名字
        //   然后再通过 ClassLoader.getResource 来定位资源，所以对于  /(根路径) 开头的查找是可以找见的。

        //   区别见解：
        //   Class 是局部个体，其一定归属于某个 ClassLoader，
        //   所以处理资源名字的逻辑就是按照自己为中心，尽量传递可能正确的路径。
        //   对于 相对路径 的就拼接上自己的包名，构成完整路径交给后续 ClassLoader 来处理。
        //   对于 绝对路径 的就去掉前导 / ，保证 ClassLoader 可以获得一个正确的路径。

        //   而 ClassLoader 是一个全局的资源拥有者，所以他自己就代表资源查找的 /(根路径)，
        //   故没有必要在路径中再加入了，所有可能存在于 ClassLoader 的资源对于他来说都是相对的。
        //   因此 Class 有相对决定路径的概念，ClassLoader 却没有这个概念。


//        public java.net.URL getResource(String name) {
//            name = resolveName(name);
//            ClassLoader cl = getClassLoader0();
//            if (cl==null) {
//                 A system class.
//                return ClassLoader.getSystemResource(name);
//            }
//            return cl.getResource(name);
//        }

        URL classResource1 = urlClassLoader.getClass().getResource("");
        System.out.println(classResource1);

        URL classResource2 = urlClassLoader.getClass().getResource("/");
        System.out.println(classResource2);

        URL classResource3 = urlClassLoader.getClass().getResource("/writerflushclose.jsp");
        System.out.println(classResource3);

        URL classResource4 = urlClassLoader.getClass().getResource("writerflushclose.jsp");
        System.out.println(classResource4);

//        /**
//         * Add a package name prefix if the name is not absolute, Remove leading "/" if name is absolute
//         */
//        private String resolveName(String name) {
//            if (name == null) {
//                return name;
//            }
//            if (!name.startsWith("/")) {
//                Class<?> c = this;
//                while (c.isArray()) {
//                    c = c.getComponentType();
//                }
//                String baseName = c.getName();
//                int index = baseName.lastIndexOf('.');
//                if (index != -1) {
//                    name = baseName.substring(0, index).replace('.', '/')
//                            +"/"+name;
//                }
//            } else {
//                name = name.substring(1);
//            }
//            return name;
//        }

    }


}
