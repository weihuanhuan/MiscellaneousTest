package log.log4j2.BESBug;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;

import log.log4j2.Constant;

/**
 * Created by JasonFitch on 12/25/2018.
 */
public class BESBugTestMultiClass {


    public static void main(String[] args) throws InterruptedException, IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        File confFile = new File(Constant.CONF_FILE);

        InputStream is = new FileInputStream(confFile);
        ConfigurationSource configurationSource = new ConfigurationSource(is);
        Configurator.initialize(null, configurationSource);

        URL urlResource = confFile.toURI().toURL();
        File mainFile = new File(Constant.MAIN_CLASS);
        URL urlMain = mainFile.toURI().toURL();
        ClassLoader extClassLoader = ClassLoader.getSystemClassLoader().getParent();
        ClassLoader urlClassLoader = new URLClassLoader(new URL[]{urlResource, urlMain}, extClassLoader);

        Thread.currentThread().setContextClassLoader(urlClassLoader);

        Class<?> aClass = urlClassLoader.loadClass("log.log4j2.BESBug.BESBugTestMainClass");
        Method main = aClass.getMethod("main", new Class[]{String[].class});
        main.invoke(null, new Object[]{new String[]{""}});


//         默认配置下异步会丢失某些日志，延时让程序写完日志事件
        TimeUnit.MILLISECONDS.sleep(1000 * 60 * 24);

//        logger.error("end");


    }

}
