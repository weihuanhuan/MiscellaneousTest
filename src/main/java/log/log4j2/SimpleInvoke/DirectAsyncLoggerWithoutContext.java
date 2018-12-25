package log.log4j2.SimpleInvoke;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;
import log.log4j2.Constant;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.async.AsyncLogger;
import org.apache.logging.log4j.core.async.AsyncLoggerContext;
import org.apache.logging.log4j.core.async.AsyncLoggerDisruptor;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.xml.XmlConfiguration;
import org.apache.logging.log4j.message.MessageFactory2;
import org.apache.logging.log4j.message.ParameterizedMessageFactory;

/**
 * Created by JasonFitch on 12/25/2018.
 */
public class DirectAsyncLoggerWithoutContext {


    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException, InterruptedException, IOException {

        MessageFactory2 parameterizedMessageFactory = new ParameterizedMessageFactory();

//        AsyncLoggerContext asyncLoggerContext = new AsyncLoggerContext("BESAsyncLoggerContext");
//        asyncLoggerContext.start();

        LoggerContext loggerContext = new LoggerContext("BESLoggerContext");
//        loggerContext.start();


        File file = new File(Constant.CONF_FILE);
        ConfigurationSource configurationSource = new ConfigurationSource(new FileInputStream(file));
        Configurator.initialize(null, configurationSource);

        XmlConfiguration xmlConfiguration = new XmlConfiguration(loggerContext, configurationSource);

        AsyncLoggerDisruptor asyncLoggerDisruptor = new AsyncLoggerDisruptor("BESAsyncLoggerDisruptor");
        asyncLoggerDisruptor.start();

        Logger asyncLogger = new AsyncLogger(loggerContext, "BESAsyncLogger", parameterizedMessageFactory, asyncLoggerDisruptor);

        asyncLogger.error("hello");
        asyncLogger.error("username:{} password:{}", "user", "passwd");


        TimeUnit.SECONDS.sleep(60 * 60);

    }

}
