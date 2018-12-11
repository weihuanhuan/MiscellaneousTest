package log.log4j2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by JasonFitch on 12/11/2018.
 */
public class Log4j2Test {

    static {
//        System.setProperty("log4j2.debug'","true");
        System.setProperty("Log4jContextSelector","org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
    }

    private static Logger logger = LogManager.getLogger("LoggerName");

    public static void main(String[] args) {

        System.out.println(logger.toString());
        logger.error("Hello, World!");

    }
}
