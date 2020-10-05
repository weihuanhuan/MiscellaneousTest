package listener.session;

import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.logging.Logger;

public class SessionListenerUtils {

    public static void logSessionInfo(HttpSession session, Logger logger) {
        logger.fine("session.getId():" + session.getId());
        logger.fine("session.isNew():" + session.isNew());

        Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String key = attributeNames.nextElement();
            Object value = session.getAttribute(key);
            logger.fine(("session.getAttribute(key):" + key + "=" + value));
        }
    }

}
