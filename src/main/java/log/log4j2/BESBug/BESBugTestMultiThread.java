package log.log4j2.BESBug;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.TimeUnit;
import log.log4j2.Constant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;

/**
 * Created by JasonFitch on 12/25/2018.
 */
public class BESBugTestMultiThread {


    public static void main(String[] args) throws InterruptedException, IOException {

        Logger logger = LogManager.getLogger(BESBugTestMultiThread.class);

        new Thread(() -> {

            try {

                File confFile = new File(Constant.CONF_FILE);

                URL urlResource = confFile.toURI().toURL();
                ClassLoader extClassLoader = ClassLoader.getSystemClassLoader().getParent();
                ClassLoader urlClassLoader = new URLClassLoader(new URL[]{urlResource}, extClassLoader);

                InputStream is = new FileInputStream(confFile);
                ConfigurationSource configurationSource = new ConfigurationSource(is);
                Configurator.initialize(urlClassLoader, configurationSource);

                Logger notAsyncNotRootLogger1 = LogManager.getLogger("NotAsyncNotRootLogger1");

                while (true) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    notAsyncNotRootLogger1.error("Hello notAsyncNotRootLogger1, level error!");
                }


            } catch (IOException e) {
                e.printStackTrace();
            }


        }).start();


        while (true) {
            TimeUnit.MILLISECONDS.sleep(100);
            logger.error("Hello BESBugTestMultiThread, level error!");
        }
        // 默认配置下异步会丢失某些日志，延时让程序写完日志事件
//        TimeUnit.MILLISECONDS.sleep(1000 * 60 * 24);

//        logger.error("end");


    }

}
