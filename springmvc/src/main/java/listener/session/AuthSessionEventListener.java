package listener.session;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.logging.Logger;

public class AuthSessionEventListener implements HttpSessionListener {

    Logger logger = Logger.getLogger(this.getClass().getName());

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        logger.fine("listener.session.AuthSessionEventListener.sessionCreated");
        printHttpSessionEventInfo(se);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        logger.fine("listener.session.AuthSessionEventListener.sessionDestroyed");
        printHttpSessionEventInfo(se);
    }

    public void printHttpSessionEventInfo(HttpSessionEvent se) {
        logger.fine(se.toString());

        HttpSession session = se.getSession();
        if (session != null) {
            SessionListenerUtils.logSessionInfo(session, logger);
        }
    }

}
