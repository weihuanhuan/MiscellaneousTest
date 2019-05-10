package clazzloader;

import java.util.logging.Logger;

/**
 * Created by JasonFitch on 3/8/2019.
 */
public class ClassInitTest {


    private static Logger logger = Logger.getLogger(ClassInitTest.class.getName());

    static {
        logger.info("class static init");
    }

    public static final String finalStaticStr = "static final str";

    public static String staticStr = "static str";

    public static volatile boolean start = false;

    public static void setStart(boolean start) {
        ClassInitTest.start = start;
    }

    public static boolean getStart() {
        return start;
    }

    {
        logger.info("instance init");
    }

    public ClassInitTest() {
        logger.info("constructor init");
    }
}

