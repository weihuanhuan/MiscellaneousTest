package spring.jackson.type;

import spring.jackson.bean.RedisBase;
import spring.jackson.bean.RedisCluster;
import spring.jackson.bean.RedisMaster;
import spring.jackson.bean.RedisSentinel;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public enum RedisBaseType {

    REDIS_MASTER(Arrays.asList(ModeType.MASTER_SLAVE, ModeType.SENTINEL), Arrays.asList(RoleType.MASTER, RoleType.REPLICA), RedisMaster.class),
    REDIS_SENTINEL(Collections.singletonList(ModeType.SENTINEL), Collections.singletonList(RoleType.SENTINEL), RedisSentinel.class),
    REDIS_CLUSTER(Collections.singletonList(ModeType.CLUSTER), Arrays.asList(RoleType.MASTER, RoleType.REPLICA), RedisCluster.class);

    private final List<ModeType> modeTypes;
    private final List<RoleType> roleTypes;
    private final Class<? extends RedisBase> redisClazz;

    RedisBaseType(List<ModeType> modeTypes, List<RoleType> roleTypes, Class<? extends RedisBase> redisClazz) {
        this.modeTypes = modeTypes;
        this.roleTypes = roleTypes;
        this.redisClazz = redisClazz;
    }

    public Class<? extends RedisBase> getRedisClazz() {
        return redisClazz;
    }

    public static RedisBaseType form(RedisBase redisBase) {
        Objects.requireNonNull(redisBase, "redis base cannot be null!");

        String mode = redisBase.getMode();
        String role = redisBase.getRole();
        return form(mode, role);
    }

    public static RedisBaseType form(String mode, String role) {
        ModeType modeType = ModeType.from(mode);
        RoleType roleType = RoleType.from(role);

        for (RedisBaseType value : RedisBaseType.values()) {
            if (value.modeTypes.contains(modeType) && value.roleTypes.contains(roleType)) {
                return value;
            }
        }

        String format = String.format("cannot determine RedisBaseType with mode [%s] and role [%s]", mode, role);
        throw new IllegalArgumentException(format);
    }

}