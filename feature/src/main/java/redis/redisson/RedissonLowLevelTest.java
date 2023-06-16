package redis.redisson;

import org.redisson.client.RedisClient;
import org.redisson.client.RedisClientConfig;
import org.redisson.client.RedisConnection;
import org.redisson.client.codec.StringCodec;
import org.redisson.client.protocol.RedisCommands;

public class RedissonLowLevelTest {

    public static void main(String[] args) {

        RedisClientConfig redisClientConfig = new RedisClientConfig();
        redisClientConfig.setAddress("192.168.56.10", 6379);
        redisClientConfig.setPassword("123456");

        RedisClient client = RedisClient.create(redisClientConfig);

        RedisConnection conn = client.connect();

        String foo = conn.sync(StringCodec.INSTANCE, RedisCommands.GET, "foo");
        System.out.println("foo=" + foo);

        conn.closeAsync();
        client.shutdown();
    }

}
