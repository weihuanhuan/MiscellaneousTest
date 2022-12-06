package collection.property.recursive;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

public class ExecuteConfigFactory {

    private static final String KEY_SEPARATOR = ".";

    private static final String KEY_START = "exec";

    private static final String BIN_PATTERN = "bin.pattern";
    private static final String ARGS_PREFIX = "args.prefix";
    private static final String ARGS_SUFFIX = "args.suffix";

    private static final String EXECUTE_CONFIG_PATTERN = "^" + Pattern.quote(KEY_START) + "\\..+?\\."
            + "(" + Pattern.quote(BIN_PATTERN) + "|" + Pattern.quote(ARGS_PREFIX) + "|" + Pattern.quote(ARGS_SUFFIX) + ")$";

    private final Properties properties;

    public ExecuteConfigFactory(Properties properties) {
        this.properties = properties;
    }

    public List<ExecuteConfig> toExecuteConfigs() {
        List<ExecuteConfig> list = new ArrayList<>();

        Map<String, Map<String, String>> executeConfigMaps = getExecuteConfigMaps();

        for (Map.Entry<String, Map<String, String>> stringMapEntry : executeConfigMaps.entrySet()) {
            if (stringMapEntry == null) {
                continue;
            }

            String key = stringMapEntry.getKey();
            Map<String, String> value = stringMapEntry.getValue();
            if (key == null || value == null) {
                continue;
            }

            String binPattern = value.get(BIN_PATTERN);
            String argsPrefix = value.get(ARGS_PREFIX);
            String argsSuffix = value.get(ARGS_SUFFIX);
            ExecuteConfig executeConfig = new ExecuteConfig(key, binPattern, argsPrefix, argsSuffix);
            list.add(executeConfig);
        }
        return list;
    }

    /**
     * parse
     * <p>
     * exec.xxx.bin.aaa
     * exec.xxx.args.bbb
     * <p>
     * to
     * <p>
     * --mapKey
     * ----exec.xxx
     * <p>
     * --mapValue
     * ----bin.aaa
     * ----args.bbb
     */
    public Map<String, Map<String, String>> getExecuteConfigMaps() {
        if (properties == null) {
            return Collections.emptyMap();
        }

        Map<String, Map<String, String>> executeConfigMaps = new HashMap<>();
        for (String propertyName : this.properties.stringPropertyNames()) {
            if (!propertyName.matches(EXECUTE_CONFIG_PATTERN)) {
                continue;
            }

            //查找 exec.xxx. 的位置
            int firstKeySeparatorIndex = propertyName.indexOf(KEY_SEPARATOR);
            int secondKeySeparatorIndex = propertyName.indexOf(KEY_SEPARATOR, firstKeySeparatorIndex + KEY_SEPARATOR.length());

            //使用 propertyName 中前两个 key 域，作为 mapKey 值，来聚合一个配置组的所有配置项，并记录到 mapValue 中
            String mapKey = propertyName.substring(0, secondKeySeparatorIndex);
            Map<String, String> mapValue = executeConfigMaps.computeIfAbsent(mapKey, k -> new HashMap<>());

            //使用 propertyName 中的排除了前两个 key 域的其余 key 域，作为 subKey ，来表示一个配置组的某一个配置项的 subKey
            String subKey = propertyName.substring(secondKeySeparatorIndex + KEY_SEPARATOR.length());
            //翻译原始 propertyName 所对应的 value ，作为 subValue ，将其与 subKey 进行关联。
            String subValue = properties.getProperty(propertyName);
            mapValue.put(subKey, subValue);
        }
        return executeConfigMaps;
    }

}
