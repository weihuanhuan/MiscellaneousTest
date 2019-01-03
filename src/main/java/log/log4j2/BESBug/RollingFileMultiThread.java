package log.log4j2.BESBug;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import log.log4j2.Constant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;

/**
 * Created by JasonFitch on 12/25/2018.
 */
public class RollingFileMultiThread {


    public static void main(String[] args) throws InterruptedException, IOException {

        new Thread(() -> {

            try {

                TimeUnit.SECONDS.sleep(1);

                List<URL> urls = new ArrayList<>();
//                URL urlClassPath = new File(Constant.CLASS_PATH).toURI().toURL();
//                URL urlLog4j2API = new File(Constant.LOG4J_API_JAR_FILE).toURI().toURL();
//                URL urlLog4j2Core = new File(Constant.LOG4J_CORE_JAR_FILE).toURI().toURL();
//                URL urlDisruptor = new File(Constant.DISRUPTOR_JAR_FILE).toURI().toURL();
//                urls.add(urlClassPath);
//                urls.add(urlLog4j2API);
//                urls.add(urlLog4j2Core);
//                urls.add(urlDisruptor);

                ClassLoader appClassLoader = ClassLoader.getSystemClassLoader();
                ClassLoader extClassLoader = appClassLoader.getParent();
                ClassLoader urlClassLoader = new URLClassLoader(urls.toArray(new URL[urls.size()]), extClassLoader);

                // 更换类加载器为url classloader，发现LogManager的实例还是只有一个，
                // debug时，发现LogManager还是app classloader加载的，为什么呢？
                Thread.currentThread().setContextClassLoader(urlClassLoader);
                //JVM的类加载使用如下方法
                //  java.lang.ClassLoader.loadClass(java.lang.String, boolean)


                File confFile = new File(Constant.CONF_FILE);
                InputStream is = new FileInputStream(confFile);

                ConfigurationSource configurationSource = new ConfigurationSource(is);

                //此句 不会 导致log4j2第2次加载，不会产生新的句柄，为什么不指定自定义的url类加载器就不会2次加载呢？
                //Configurator.initialize(null, configurationSource);
                //上下文创建机制：
                //   这里的classloader实际上指定了ClassLoaderContextSelector.locateContext(classloader,URL)，方法中参数的classloader，
                //   最终如果确实新创建了一个context，那么这个classloader会产生一个唯一的id，并与刚刚新建的context相对应着,并保存在一个map中作为以后的缓冲
                //   org.apache.logging.log4j.core.selector.ClassLoaderContextSelector.CONTEXT_MAP，
                //  (不是使用先前缓冲的context，应为locateContext会在缓冲中按照classloader查找先前已经创建的context，查找范围是：递归优先寻找父加载器，一直查到自己)，
                //  这里不指定其他的类加载器，导致第二次获取context时使用的是先前创建的context所以没有使log4j2加载第二次。

                //   configurationSource是指定了ConfigurationFactory.getConfiguration(LoggerContext, ConfigurationSource)的第二个参数
                //   其是在context确定以后，用来确定配置按照什么来源生成的。
                //   而URL，目前各种情况下都为null，具体作用未知。

                //此句 会 导致log4j2第2次加载，不会产生新的句柄，为什么这里的加载不会创建新的句柄，相比于BESBugTestMultiClass测试的情况？
                Configurator.initialize(urlClassLoader, configurationSource);
                //缓冲机制：
                //   首先注意这里的LogManager的加载都是由app classloader去加载的，即LogManager的实例只有一个
                //   而urlClassLoader只是在处理log4j2的context时才会使用，所以log4j2会加载第二次，
                //   但是，org.apache.logging.log4j.core.appender.AbstractManager的AbstractManager.getManager()方法
                //   在取manager时会在其成员变量MAP中优先使用之前创建的已经缓冲的管理器，如果没有找到先前缓冲的管理器才会新创建一个,
                //   所以在只有一个类加载器加载LogManager时，多个RollingFileAppender不会重复打开日志文件IO流，
                //   而是利用流管理器统一对文件的读写进行处理，因此这里不会创建新的句柄，只有一个文件handler，

                //   这是不是也是log4j2在写入文件数据时，不是对目的地lock，反而是去lock目的地所对应的管理器的原因？


                TimeUnit.SECONDS.sleep(60*60);


                //正常运行时没有这个问题，debug时出现如下异常为什么，出现时机main和sub线程同时停在ClassLoaderContextSelector.locateContext():112处时，F9恢复程序，
//                2019-01-02 05:46:17,251 main ERROR Could not register mbeans javax.management.InstanceAlreadyExistsException: org.apache.logging.log4j2:type=18b4aac2
//                at com.sun.jmx.mbeanserver.Repository.addMBean(Repository.java:437)
//                at com.sun.jmx.interceptor.DefaultMBeanServerInterceptor.registerWithRepository(DefaultMBeanServerInterceptor.java:1898)
//                at com.sun.jmx.interceptor.DefaultMBeanServerInterceptor.registerDynamicMBean(DefaultMBeanServerInterceptor.java:966)
//                at com.sun.jmx.interceptor.DefaultMBeanServerInterceptor.registerObject(DefaultMBeanServerInterceptor.java:900)
//                at com.sun.jmx.interceptor.DefaultMBeanServerInterceptor.registerMBean(DefaultMBeanServerInterceptor.java:324)
//                at com.sun.jmx.mbeanserver.JmxMBeanServer.registerMBean(JmxMBeanServer.java:522)
//                at org.apache.logging.log4j.core.jmx.Server.register(Server.java:393)
//                at org.apache.logging.log4j.core.jmx.Server.reregisterMBeansAfterReconfigure(Server.java:168)
//                at org.apache.logging.log4j.core.jmx.Server.reregisterMBeansAfterReconfigure(Server.java:141)
//                at org.apache.logging.log4j.core.LoggerContext.setConfiguration(LoggerContext.java:558)
//                at org.apache.logging.log4j.core.LoggerContext.reconfigure(LoggerContext.java:619)
//                at org.apache.logging.log4j.core.LoggerContext.reconfigure(LoggerContext.java:636)
//                at org.apache.logging.log4j.core.LoggerContext.start(LoggerContext.java:231)
//                at org.apache.logging.log4j.core.impl.Log4jContextFactory.getContext(Log4jContextFactory.java:153)
//                at org.apache.logging.log4j.core.impl.Log4jContextFactory.getContext(Log4jContextFactory.java:45)
//                at org.apache.logging.log4j.LogManager.getContext(LogManager.java:194)
//                at org.apache.logging.log4j.LogManager.getLogger(LogManager.java:581)
//                at log.log4j2.BESBug.RollingFileMultiThread.main(RollingFileMultiThread.java:88)


            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }, "subThread").start();


        TimeUnit.SECONDS.sleep(10);


        //JF log4j2第第一次加载,产生第1个文件句柄，
        //   如果其配置文件开启了monitorInterval，则会创建一个WatchManager实例，
        //   而该实例会创建Log4j2-TF-4-Scheduled-x线程监控配置文件的变化
        //   其创建过程如下示：
//        "main@1" prio=5 tid=0x1 nid=NA runnable
//        java.lang.Thread.State: RUNNABLE
//        at org.apache.logging.log4j.core.config.ConfigurationScheduler.getExecutorService(ConfigurationScheduler.java:199)
//        at org.apache.logging.log4j.core.config.ConfigurationScheduler.scheduleWithFixedDelay(ConfigurationScheduler.java:187)
//        at org.apache.logging.log4j.core.util.WatchManager.start(WatchManager.java:122)
//        at org.apache.logging.log4j.core.config.AbstractConfiguration.start(AbstractConfiguration.java:255)
//        at org.apache.logging.log4j.core.LoggerContext.setConfiguration(LoggerContext.java:547)
//        at org.apache.logging.log4j.core.LoggerContext.reconfigure(LoggerContext.java:619)
//        at org.apache.logging.log4j.core.LoggerContext.reconfigure(LoggerContext.java:636)
//        at org.apache.logging.log4j.core.LoggerContext.start(LoggerContext.java:231)
//        at org.apache.logging.log4j.core.impl.Log4jContextFactory.getContext(Log4jContextFactory.java:153)
//        at org.apache.logging.log4j.core.impl.Log4jContextFactory.getContext(Log4jContextFactory.java:45)
//        at org.apache.logging.log4j.LogManager.getContext(LogManager.java:194)
//        at org.apache.logging.log4j.LogManager.getLogger(LogManager.java:581)
//        at log.log4j2.BESBug.RollingFileMultiThread.main(RollingFileMultiThread.java:30)

        Logger logger = LogManager.getLogger(RollingFileMultiThread.class);
        logger.error("Hello RollingFileMultiThread, level error!");

        TimeUnit.SECONDS.sleep(60*60);
    }

}
