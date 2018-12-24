package log.log4j2;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;
import javax.websocket.RemoteEndpoint;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.async.AsyncLogger;
import org.apache.logging.log4j.core.async.AsyncLoggerContext;
import org.apache.logging.log4j.core.async.AsyncLoggerDisruptor;
import org.apache.logging.log4j.message.MessageFactory2;
import org.apache.logging.log4j.message.ParameterizedMessageFactory;

/**
 * Created by JasonFitch on 12/24/2018.
 */
public class DirectAsyncLogger {


    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException, InterruptedException {


        MessageFactory2 parameterizedMessageFactory = new ParameterizedMessageFactory();

        AsyncLoggerContext asyncLoggerContext = new AsyncLoggerContext("BESAsyncContext");
        asyncLoggerContext.start();

        Field loggerDisruptorFiled = asyncLoggerContext.getClass().getDeclaredField("loggerDisruptor");
        loggerDisruptorFiled.setAccessible(true);
        AsyncLoggerDisruptor asyncLoggerDisruptor = (AsyncLoggerDisruptor) loggerDisruptorFiled.get(asyncLoggerContext);

        Logger asyncLogger = new AsyncLogger(asyncLoggerContext, "BESAsyncLogger", parameterizedMessageFactory, asyncLoggerDisruptor);

        asyncLogger.error("hello");
        asyncLogger.error("username:{} password:{}", "user", "passwd");


        TimeUnit.SECONDS.sleep(60*60);


    }
}
