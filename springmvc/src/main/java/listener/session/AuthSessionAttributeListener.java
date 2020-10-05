package listener.session;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import java.util.logging.Logger;

public class AuthSessionAttributeListener implements HttpSessionAttributeListener {

    Logger logger = Logger.getLogger(this.getClass().getName());

    @Override
    public void attributeAdded(HttpSessionBindingEvent event) {
        logger.fine("listener.session.AuthSessionAttributeListener.attributeAdded");
        printSessionBindingEvent(event);
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent event) {
        printSessionBindingEvent(event);
        logger.fine("listener.session.AuthSessionAttributeListener.attributeRemoved");
    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent event) {
        logger.fine("listener.session.AuthSessionAttributeListener.attributeReplaced");
        printSessionBindingEvent(event);
    }

    private void printSessionBindingEvent(HttpSessionBindingEvent event) {
        logger.fine(event.toString());

        String name = event.getName();
        if (name != null) {
            logger.fine(name);
        }

        Object value = event.getValue();
        if (value != null) {
            logger.fine(value.toString());
        }

        HttpSession session = event.getSession();
        if (session != null) {
            SessionListenerUtils.logSessionInfo(session, logger);
        }
    }

}
