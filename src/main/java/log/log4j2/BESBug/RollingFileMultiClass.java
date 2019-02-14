package log.log4j2.BESBug;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;

import log.log4j2.Constant;

/**
 * Created by JasonFitch on 12/25/2018.
 */
public class RollingFileMultiClass {

    static {
        System.setProperty("Log4jContextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
    }

    public static void main(String[] args) throws InterruptedException, IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        //JF 使用系统的app classloader
        //   这里log4j2 初次 加载配置，并 首次打开 要轮询的文件log文件，产生第1个文件句柄
        //   在org.apache.logging.log4j.core.appender.rolling.RollingFileManager.RollingFileManagerFactory.createManager()方法中。
        File confFile = new File(Constant.CONF_FILE);
        InputStream is = new FileInputStream(confFile);
        ConfigurationSource configurationSource = new ConfigurationSource(is);
        Configurator.initialize(null, configurationSource);


        Logger notAsyncNotRootLogger1 = LogManager.getLogger("NotAsyncNotRootLogger1");
        //JF 只有真正的写日志记录时，才会进行轮询的判断，并处理轮询相关的文件操作，
        //   这里先行轮询一次可以防止在第二次时轮询时文件被占用，是否合理？
        //   答案不合理，因为始终都是文件被打开两次，轮询时只能关闭一次的，那么就一定会出现问题。
        notAsyncNotRootLogger1.error("Hello notAsyncNotRootLogger1, level error!");


        //Configurator.initialize()方法触发， 打开日志文件句柄流程
//        "main@1" prio=5 tid=0x1 nid=NA runnable
//        java.lang.Thread.State: RUNNABLE
//        at java.io.FileOutputStream.<init>(FileOutputStream.java:133)
//        at org.apache.logging.log4j.core.appender.rolling.RollingFileManager$RollingFileManagerFactory.createManager(RollingFileManager.java:641)
//        at org.apache.logging.log4j.core.appender.rolling.RollingFileManager$RollingFileManagerFactory.createManager(RollingFileManager.java:608)
//        at org.apache.logging.log4j.core.appender.AbstractManager.getManager(AbstractManager.java:113)
//        at org.apache.logging.log4j.core.appender.OutputStreamManager.getManager(OutputStreamManager.java:114)
//        at org.apache.logging.log4j.core.appender.rolling.RollingFileManager.getFileManager(RollingFileManager.java:188)
//        at org.apache.logging.log4j.core.appender.RollingFileAppender$Builder.build(RollingFileAppender.java:145)
//        at org.apache.logging.log4j.core.appender.RollingFileAppender$Builder.build(RollingFileAppender.java:61)
//        at org.apache.logging.log4j.core.config.plugins.util.PluginBuilder.build(PluginBuilder.java:123)
//        at org.apache.logging.log4j.core.config.AbstractConfiguration.createPluginObject(AbstractConfiguration.java:959)
//        at org.apache.logging.log4j.core.config.AbstractConfiguration.createConfiguration(AbstractConfiguration.java:899)
//        at org.apache.logging.log4j.core.config.AbstractConfiguration.createConfiguration(AbstractConfiguration.java:891)
//        at org.apache.logging.log4j.core.config.AbstractConfiguration.doConfigure(AbstractConfiguration.java:514)
//        at org.apache.logging.log4j.core.config.AbstractConfiguration.initialize(AbstractConfiguration.java:238)
//        at org.apache.logging.log4j.core.config.AbstractConfiguration.start(AbstractConfiguration.java:250)
//        at org.apache.logging.log4j.core.LoggerContext.setConfiguration(LoggerContext.java:547)
//        at org.apache.logging.log4j.core.LoggerContext.start(LoggerContext.java:263)
//        at org.apache.logging.log4j.core.impl.Log4jContextFactory.getContext(Log4jContextFactory.java:179)
//        at org.apache.logging.log4j.core.config.Configurator.initialize(Configurator.java:86)
//        at org.apache.logging.log4j.core.config.Configurator.initialize(Configurator.java:67)
//        at log.log4j2.BESBug.RollingFileMultiClass.main(RollingFileMultiClass.java:34)

        //JF 构造自定义url classloader的类路径
        // 如若没有将所需的类加入到类类路径中就会发生 ClassNotFoundException
        // Caused by: java.lang.ClassNotFoundException: org.apache.logging.log4j.LogManager
        List<URL> urls = new ArrayList<>();
        URL urlClassPath = new File(Constant.CLASS_PATH).toURI().toURL();
        URL urlLog4j2API = new File(Constant.LOG4J_API_JAR_FILE).toURI().toURL();
        URL urlLog4j2Core = new File(Constant.LOG4J_CORE_JAR_FILE).toURI().toURL();
        URL urlDisruptor = new File(Constant.DISRUPTOR_JAR_FILE).toURI().toURL();
        urls.add(urlClassPath);
        urls.add(urlLog4j2API);
        urls.add(urlLog4j2Core);
        urls.add(urlDisruptor);

        //使用ext classLoader作为自定义url的类加载器，避免该类加载器经过app classloader去加载资源，因为双亲委派模型的存在
        ClassLoader appClassLoader = ClassLoader.getSystemClassLoader();
        ClassLoader extClassLoader = appClassLoader.getParent();
        ClassLoader urlClassLoader = new URLClassLoader(urls.toArray(new URL[urls.size()]), extClassLoader);

        //JF 上下文线程 classloader 更换为自定义的url classloader，其类路径和app的不同,由上方url数组指定
        Thread.currentThread().setContextClassLoader(urlClassLoader);
        //JF 如果不更换上下文线程 classloader 第二次加载也是由url去加载，只是会抛出如下异常，为什么呢？ContextClassLoader会加载哪些资源？
//        Exception in thread "main" java.lang.reflect.InvocationTargetException
//        at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
//        at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
//        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
//        at java.lang.reflect.Method.invoke(Method.java:498)
//        at log.log4j2.BESBug.RollingFileMultiClass.main(RollingFileMultiClass.java:93)
//        Caused by: java.lang.NoClassDefFoundError: Could not initialize class org.apache.logging.log4j.util.PropertiesUtil
//        at org.apache.logging.log4j.status.StatusLogger.<clinit>(StatusLogger.java:78)
//        at org.apache.logging.log4j.LogManager.<clinit>(LogManager.java:60)
//        at log.log4j2.BESBug.RollingFileInvokedMainClass.main(RollingFileInvokedMainClass.java:27)
//	... 5 more

        //JF 接下来的资源加载都是使用 更换后的url类加载器去加载的，这些资源指的是哪些资源？
        Class<?> mainClass = urlClassLoader.loadClass(Constant.MAIN_CLASS_NAME);
        Method mainMethod = mainClass.getMethod("main", new Class[]{String[].class});
        mainMethod.invoke(null, new Object[]{new String[]{}});

        Class.forName(Constant.MAIN_CLASS_NAME);

        System.out.println("end");
    }

}
