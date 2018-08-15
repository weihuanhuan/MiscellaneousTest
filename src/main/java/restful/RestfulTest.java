package restful;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedWriter;
import java.io.PrintWriter;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

/**
 * Created by JasonFitch on 8/13/2018.
 */


public class RestfulTest
{
    public static void main(String[] args)
    {

        ClientBuilder builder = ClientBuilder.newBuilder();
        Client jerseyClient = builder.build();

    }
}
