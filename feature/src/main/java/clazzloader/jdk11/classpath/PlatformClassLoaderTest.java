package clazzloader.jdk11.classpath;

//import jdk.internal.loader.URLClassPath;//jdk11

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessControlContext;

public class PlatformClassLoaderTest {

    /**
     * test target data
     */
    private static final String targetClassName = "org.antlr.v4.Tool";
    private static final String targetJarFile = "C:/antlr/antlr-4.5.3-complete.jar";

    /**
     * reflect data
     */
    private static final String ucpFieldName = "ucp";

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException, MalformedURLException,
            ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException {

        System.out.println("############## testRuntime ##############");
        PlatformClassLoaderTest.testRuntime();

//        System.out.println("############## testCompile ##############");
//        PlatformClassLoaderTest.testCompile();
    }

    /**
     * runtime 场景只需要向 java 命令配置如下的选项
     * --add-opens java.base/jdk.internal.loader=ALL-UNNAMED
     *
     * @see jdk.internal.loader.BuiltinClassLoader#ucp
     * @see jdk.internal.loader.URLClassPath#addURL(java.net.URL)
     */
    private static void testRuntime() throws InvocationTargetException, InstantiationException, IllegalAccessException,
            NoSuchFieldException, NoSuchMethodException, MalformedURLException, ClassNotFoundException {

        ClassLoader platformClassLoader = getPlatformClassLoader();

        Field ucpField = getUcpField(platformClassLoader, ucpFieldName);

        Object ucpFieldValue = getUcpFieldValue(ucpField, platformClassLoader);

        //兼容 jdk8 和 jdk11
        //在 jdk8 中 ，platformClassLoader 对应 extClassLoader, 其父类 URLClassLoader 的 ucp 域不为 null，且默认存在 jdk_home/jre/lib/ext/*.jar 下面的所有 jar 包
        // 此时我们应该使用原先的 ucp 域的实例，以保持原先存在 extClassLoader 需要加载的 jar 不变，然后在其中添加入我们的动态 jar 包。
        //而在 jdk11 中，platformClassLoader 代替了 extClassLoader，其父类 BuiltinClassLoader 的 ucp 域默认为 null，
        // 此时我们才需要新创建一个 ucp 域的实例，然后在其中添加入我们的动态 jar 包。
        if (ucpFieldValue == null) {
            //创建 ucp 域的实例 jdk.internal.loader.URLClassPath.URLClassPath
            Class<?> type = ucpField.getType();
            Constructor<?> constructor = type.getConstructor(URL[].class, AccessControlContext.class);
            ucpFieldValue = constructor.newInstance(new URL[0], null);
            setUcpFieldValue(ucpField, platformClassLoader, ucpFieldValue);
        }

        URL jarUrl = getJarUrl(targetJarFile);

        //使用 addURL 方法添加 jar 到 ucp 域的实例中以供加载
        String addURLMethodName = "addURL";
        Method addURLMethod = ucpFieldValue.getClass().getMethod(addURLMethodName, URL.class);
        addURLMethod.invoke(ucpFieldValue, jarUrl);

        loadAndPrintInfo(platformClassLoader, targetClassName);
    }

    /**
     * compile 场景首先需要向 javac 命令配置如下的选项
     * 移除 --release 选项
     * <p>
     * 然后在 runtime 时还需要向 java 命令配置如下的选项
     * --add-opens java.base/jdk.internal.loader=ALL-UNNAMED
     */
//    private static void testCompile() throws IllegalAccessException, NoSuchFieldException, MalformedURLException, ClassNotFoundException {
//        ClassLoader platformClassLoader = getPlatformClassLoader();
//
//        Field ucpField = getUcpField(platformClassLoader, ucpFieldName);
//
//        URLClassPath ucpFieldValue = (URLClassPath) getUcpFieldValue(ucpField, platformClassLoader);
//
//        //检测是否已经被设置过 ucp 域的实例了，仅当其为 null 时我们才进行创建，否则直接使用原先设置的即可。
//        if (ucpFieldValue == null) {
//            //创建 ucp 域的实例 jdk.internal.loader.URLClassPath.URLClassPath
//            ucpFieldValue = new URLClassPath(new URL[0], null);
//            setUcpFieldValue(ucpField, platformClassLoader, ucpFieldValue);
//        }
//
//        URL jarUrl = getJarUrl(targetJarFile);
//
//        //使用 addURL 方法添加 jar 到 ucp 域的实例中以供加载
//        ucpFieldValue.addURL(jarUrl);
//
//        loadAndPrintInfo(platformClassLoader, targetClassName);
//    }

    /**
     * jdk8 和 jdk11 的 java.lang.ClassLoader#getSystemClassLoader() 并不相同，同时对应结果的 system class loader 的 parent 也不相同
     */
    private static ClassLoader getPlatformClassLoader() {
        //获取 jdk.internal.loader.ClassLoaders.PlatformClassLoader
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        ClassLoader platformClassLoader = systemClassLoader.getParent();
        return platformClassLoader;
    }

    private static Field getUcpField(ClassLoader platformClassLoader, String ucpFieldName) throws NoSuchFieldException {
        //获取 jdk.internal.loader.BuiltinClassLoader 类的 ucp 域
        Class<?> builtinClassLoaderClazz = platformClassLoader.getClass().getSuperclass();
        Field ucpField = builtinClassLoaderClazz.getDeclaredField(ucpFieldName);
        return ucpField;
    }

    private static Object getUcpFieldValue(Field ucpField, ClassLoader platformClassLoader) throws IllegalAccessException {
        //获取 platformClassLoader 对象 的 ucp 域实例
        ucpField.setAccessible(true);
        Object urlClassPath = ucpField.get(platformClassLoader);
        return urlClassPath;
    }

    private static void setUcpFieldValue(Field ucpField, ClassLoader platformClassLoader, Object urlClassPath) throws IllegalAccessException {
        //将创建的 ucp 域实例设置给 platformClassLoader 对象
        ucpField.setAccessible(true);
        ucpField.set(platformClassLoader, urlClassPath);
    }

    private static URL getJarUrl(String fileName) throws MalformedURLException {
        //准备被加载的 jar 包
        File file = new File(fileName);
        URL url = file.toURI().toURL();
        return url;
    }

    private static void loadAndPrintInfo(ClassLoader platformClassLoader, String className) throws ClassNotFoundException {
        //测试是否可以加载刚刚添加的 jar 包的类
        Class<?> aClass = platformClassLoader.loadClass(className);

        //输出实际加载到的 class 信息
        System.out.println(aClass);
        System.out.println(aClass.getClassLoader());
    }

}
