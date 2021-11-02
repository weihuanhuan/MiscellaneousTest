package SystemTest.jdk;

import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * @see org.apache.commons.lang.SystemUtils
 */
public class JDKUtils {

    private static final int MODULARITY_JDK_VERSION = 9;

    private static final String EMPTY_STRING = "";

    private static final String BEFORE_JDK8_PREFIX = "1.";

    private static final String JAVA_SPECIFICATION_VERSION = "java.specification.version";

    public static boolean isModularityJdk() {
        try {
            String majorVersion = getMajorVersion();
            return Integer.parseInt(majorVersion) >= MODULARITY_JDK_VERSION;
        } catch (Throwable throwable) {
            return false;
        }
    }

    public static String getMajorVersion() {
        String specVersion = getSystemProperty(JAVA_SPECIFICATION_VERSION);
        return parserMajorVersion(specVersion);
    }

    private static String parserMajorVersion(String specVersion) {
        int length = BEFORE_JDK8_PREFIX.length();
        if (specVersion != null && specVersion.length() > length && specVersion.startsWith(BEFORE_JDK8_PREFIX)) {
            return specVersion.substring(length);
        } else {
            return specVersion;
        }
    }

    public static String getSystemProperty(final String name) {
        //java.lang.String.isEmpty from jdk1.6
        if (name == null || name.length() == 0) {
            return EMPTY_STRING;
        }

        String doPrivileged = AccessController.doPrivileged(new PrivilegedAction<String>() {
            //There is no @Override for compatible jdk1.5
            public String run() {
                return System.getProperty(name);
            }
        });
        return doPrivileged == null ? EMPTY_STRING : doPrivileged;
    }

}
