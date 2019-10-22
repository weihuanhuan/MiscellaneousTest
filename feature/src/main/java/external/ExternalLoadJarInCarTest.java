package external;

import java.util.logging.Logger;

/**
 * Created by JasonFitch on 10/18/2019.
 */
public class ExternalLoadJarInCarTest {
    public static Logger logger = Logger.getLogger(ExternalLoadJarInCarTest.class.getName());

    static {
        String file = ExternalLoadJarInCarTest.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        logger.info(file);

        ClassLoader classLoader = ExternalLoadJarTest.class.getClassLoader();
        while (classLoader != null) {
            logger.info(classLoader.toString());
            classLoader = classLoader.getParent();
        }
    }

}
