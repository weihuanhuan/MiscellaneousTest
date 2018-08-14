package restful;

import java.io.File;
import java.io.IOException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyWebTarget;

/**
 * Created by JasonFitch on 8/13/2018.
 */
public class RestfulTest
{
    public static void main(String[] args)
    {

        ClientBuilder builder  = ClientBuilder.newBuilder();
        Client client = builder.build();

        WebTarget wt = client.target("http://2001:db8:0:f101::1:6900/rest");
        //需要加上[]否则回出现异常 java.net.MalformedURLException



    }
}
