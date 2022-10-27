package concurrency.ThreadPoolExecutorTest;

/**
 * Created by JasonFitch on 3/14/2019.
 */
public class SimpleSession {

    private String sessionID;

    public SimpleSession(String sessionID) {
        this.sessionID = sessionID;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    @Override
    public String toString() {
        return "SimpleSession{" + "sessionID='" + sessionID + '\'' + '}';
    }
}
