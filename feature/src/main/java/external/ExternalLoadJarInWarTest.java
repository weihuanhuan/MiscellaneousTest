package external;

import java.util.logging.Logger;

/**
 * Created by JasonFitch on 10/18/2019.
 */
public class ExternalLoadJarInWarTest {
    public static Logger logger = Logger.getLogger(ExternalLoadJarInWarTest.class.getName());

    static {
        String file = ExternalLoadJarInWarTest.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        logger.info(file);

        ClassLoader classLoader = ExternalLoadJarTest.class.getClassLoader();
        while (classLoader != null) {
            logger.info(classLoader.toString());
            classLoader = classLoader.getParent();
        }
    }

}
