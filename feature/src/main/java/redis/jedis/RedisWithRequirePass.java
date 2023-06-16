package redis.jedis;

import redis.clients.jedis.Jedis;

/**
 * Created by JasonFitch on 2/14/2019.
 */
public class RedisWithRequirePass {

    public static void main(String[] args) {

        Jedis jedis;
        jedis = new Jedis("192.168.88.10", 6379);
        jedis.auth("123456");
        jedis.set("foo", "bar");
        String value = jedis.get("foo");
        System.out.println(value);

    }

}
