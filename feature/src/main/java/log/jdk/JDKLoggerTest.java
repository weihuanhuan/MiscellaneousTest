package log.jdk;

import java.util.logging.Logger;

/**
 * Created by JasonFitch on 3/22/2019.
 */
public class JDKLoggerTest {

    public static void main(String[] args) {

        Logger loggerBlank = Logger.getLogger("");

        //JF Exception in thread "main" java.util.MissingResourceException: Can't find resource bundle
        Logger loggerBlankWithRes = Logger.getLogger("","resource");

        //JF Exception in thread "main" java.lang.NullPointerException
        Logger loggerNull = Logger.getLogger(null);

        InterLogger interLogger = new InterLogger(null, null);
        System.out.println(interLogger.getName()==null);
        //JF java.lang.NullPointerException，注意日志记录器的 name 可以为 null ,
        //   故最好把常量放在表达式的前面，防止不必要的NPE问题
        if("".equals(interLogger.getName())){
            System.out.println("logger name test for null");
        }

    }

    static class InterLogger extends Logger {

        protected InterLogger(String name, String resourceBundleName) {
            super(name, resourceBundleName);
        }
    }
}
