package redis.redisson;

import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RExecutorService;
import org.redisson.api.RKeys;
import org.redisson.api.RLock;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;

import java.util.concurrent.TimeUnit;

public class RedissonTest {

    public static void main(String[] args) {

        // 1. Create config object
        Config config = new Config();
        //TODO 如果不添加 org.redisson.client.codec.StringCodec 则会出现下面的异常, 这个东西手动处理有点麻烦，不能自动的添加吗？
        // Caused by: com.esotericsoftware.kryo.KryoException: Encountered unregistered class ID: 96
        config.setCodec(new StringCodec());
        SingleServerConfig singleServerConfig = config.useSingleServer();
        singleServerConfig.setAddress("redis://192.168.56.10:6379").setPassword("123456");

        // 2. Create Redisson instance
        // Sync and Async API
        RedissonClient redisson = Redisson.create(config);

        // 3. 使用 redisson 客户端来查询 key foo 的值
        String fooKey = "foo";
        RBucket<String> bucket = redisson.getBucket(fooKey);
        String fooValues = bucket.get();
        System.out.println("fooValues=" + fooValues);

        // 3. 使用 redisson 客户端来添加 key
        String addedKey = "test-addedKey";
        String addedValue = "test-addedValue";

        RMapCache<String, String> mapCache = redisson.getMapCache("test-redis");
        String mapCachePut = mapCache.put(addedKey, addedValue, 10 * 60, TimeUnit.SECONDS);
        System.out.println("mapCachePut=" + mapCachePut);

        String mapCacheGet = mapCache.get(addedKey);
        System.out.println("mapCacheGet=" + mapCacheGet);
        long mapCacheTTL = mapCache.remainTimeToLive(addedKey);
        System.out.println("mapCacheTTL=" + mapCacheTTL);

        // 3. 使用 redisson 客户端来检测 key 的 ttl
        RKeys rkeys = redisson.getKeys();
        long keysCountExists = rkeys.countExists(addedKey);
        System.out.println("keysCountExists=" + keysCountExists);
        long keysRemainTimeToLive = rkeys.remainTimeToLive(addedKey);
        System.out.println("keysRemainTimeToLive=" + keysRemainTimeToLive);

        // 4. Get Redis based implementation of java.util.concurrent.locks.Lock
        RLock lock = redisson.getLock("myLock");

        // 4. Get Redis based implementation of java.util.concurrent.ExecutorService
        RExecutorService executor = redisson.getExecutorService("myExecutorService");

        // 5. 关闭 redisson
        redisson.shutdown();
    }

}
