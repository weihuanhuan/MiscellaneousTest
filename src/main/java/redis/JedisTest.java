package redis;

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

        Socket         sc = new Socket("[fe80::20c:29ff:fe68:65ec%7]", 6379);
        InputStream    is = sc.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String         line;
        while (null != (line = br.readLine()))
            System.out.println(line);

        Jedis jedis;
        jedis = new Jedis("[fe80::20c:29ff:fe68:65ec%7]", 6379);
//        jedis = new Jedis("[2001:db8:0:f101::1]", 6379);
        jedis.set("foo", "bar");
        String value = jedis.get("foo");
        System.out.println(value);
    }
}
