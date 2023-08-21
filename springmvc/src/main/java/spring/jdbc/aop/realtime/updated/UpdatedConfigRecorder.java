package spring.jdbc.aop.realtime.updated;

import spring.jdbc.aop.helper.RedisServiceHelper;
import spring.jdbc.mybatis.bean.MybatisRedis;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class UpdatedConfigRecorder {

    private final Map<String, UpdatedConfig> updatedConfigMap = new LinkedHashMap<>();

    public void recordUpdatedConfig(MybatisRedis newMybatisRedis) {
        if (newMybatisRedis == null) {
            return;
        }

        String name = newMybatisRedis.getName();
        if (name == null || name.isEmpty()) {
            return;
        }

        UpdatedConfig updatedConfig = updatedConfigMap.get(name);
        if (updatedConfig == null) {
            MybatisRedis oldMybatisRedis = RedisServiceHelper.getRedisByName(name);
            if (oldMybatisRedis == null) {
                return;
            }

            String mode = newMybatisRedis.getMode();
            String role = newMybatisRedis.getRole();
            String status = oldMybatisRedis.getStatus();
            updatedConfig = new UpdatedConfig(name, mode, role, status);
            updatedConfigMap.put(name, updatedConfig);

            String oldConfigText = oldMybatisRedis.getConfigText();
            updatedConfig.setOldConfigText(oldConfigText);
        }

        String newConfigText = newMybatisRedis.getConfigText();
        updatedConfig.setNewConfigText(newConfigText);
    }

    public Collection<UpdatedConfig> getUpdatedConfig() {
        Collection<UpdatedConfig> values = updatedConfigMap.values();
        return values;
    }

    public UpdatedConfig getUpdatedConfig(String name) {
        if (name == null) {
            return null;
        }

        UpdatedConfig updatedConfig = updatedConfigMap.get(name);
        return updatedConfig;
    }

    public void clearUpdatedConfig() {
        updatedConfigMap.clear();
    }

}
