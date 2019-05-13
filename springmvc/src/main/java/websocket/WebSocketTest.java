package websocket;

import java.io.IOException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 * Created by JasonFitch on 5/13/2019.
 */
@ServerEndpoint("/ws")
public class WebSocketTest {

    @OnOpen
    public void onOpen(Session session) throws IOException {
        System.out.println("Client connected");
        session.getBasicRemote().sendText("onOpen");
    }

    @OnMessage
    public void echo(Session session, String message) throws IOException {
        session.getBasicRemote().sendText(message + " (from your server)");
        session.close();
    }

    @OnClose
    public void onClose() {
        System.out.println("Connection closed");
    }

    @OnError
    public void onError(Throwable t) {
        t.printStackTrace();
    }


}
