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

                File confFile = new File(Constant.CONF_FILE);
                InputStream is = new FileInputStream(confFile);
                ConfigurationSource configurationSource = new ConfigurationSource(is);
                //此句 不会 导致log4j2第2次加载，不会产生新的句柄，为什么不指定自定义的url类加载器就不会2次加载呢？
//                Configurator.initialize(null, configurationSource);
                //此句 会 导致log4j2第2次加载，不会产生新的句柄，为什么这里的加载不会创建新的句柄，相比于BESBugTestMultiClass测试的情况？
                Configurator.initialize(urlClassLoader, configurationSource);


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

    }

}
