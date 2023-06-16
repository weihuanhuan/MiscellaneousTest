package redis.jedis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import redis.clients.jedis.Jedis;

/**
 * Created by JasonFitch on 11/6/2018.
 */
public class JedisTest {

    public static void main(String[] args) throws IOException {

        Socket sc = new Socket("[fe80::20c:29ff:fe68:65ec%7]", 6379);
        if (sc.isConnected()) {
            System.out.println("Connected " + sc.toString());
        }
//        Exception in thread "main" redis.clients.jedis.exceptions.JedisDataException:
//        DENIED Redis is running in protected mode because protected mode is enabled,
//        no bind address was specified, no authentication password is requested to clients.
//        In this mode connections are only accepted from the loopback interface.
//        If you want to connect from external computers to Redis you may adopt one of the following solutions:

//        1) Just disable protected mode sending the command 'CONFIG SET protected-mode no' from the loopback
//        interface by connecting to Redis from the same host the server is running,
//        however MAKE SURE Redis is not publicly accessible from internet if you do so.
//        Use CONFIG REWRITE to make this change permanent.

//        2) Alternatively you can just disable the protected mode by editing the Redis configuration file,
//        and setting the protected mode option to 'no', and then restarting the server.

//        3) If you started the server manually just for testing, restart it with the '--protected-mode no' option.

//        4) Setup a bind address or an authentication password.
//        NOTE: You only need to do one of the above things in order for the server to start accepting connections from the outside.
        Jedis jedis;
        jedis = new Jedis("[fe80::20c:29ff:fe68:65ec%7]", 6379);
//        jedis = new Jedis("[2001:db8:0:f101::1]", 6379);
        jedis.set("foo", "bar");
        String value = jedis.get("foo");
        System.out.println(value);
    }
}
