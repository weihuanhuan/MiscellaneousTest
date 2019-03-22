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
    }
}
