package collection.property;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.regex.Pattern;

/**
 * Created by JasonFitch on 7/20/2020.
 */
public class MapProperties extends Properties {

    @Deprecated
    Map<String, String> getMapProperties(String key) {
        Map<String, String> result = new HashMap();
        for (Map.Entry<Object, Object> entry : this.entrySet()) {
            String entryKey = (String) entry.getKey();
            String value = (String) entry.getValue();

            if (entryKey.matches("^" + Pattern.quote(key) + "\\..*\\.\\d+$")) {
                int startIndex = entryKey.indexOf(".");
                int endIndex = entryKey.lastIndexOf(".");
                String substring = entryKey.substring(startIndex + 1, endIndex);
                result.put(substring, value);
            }
        }
        return result;
    }

    Map<String, Map<String, String>> getAllMapProperties(String key) {
        Map<String, Map<String, String>> allMap = new HashMap();
        for (Map.Entry<Object, Object> entry : this.entrySet()) {
            String entryKey = (String) entry.getKey();
            if (entryKey.matches("^" + Pattern.quote(key) + "\\..*\\.\\d+$")) {
                int startIndex = entryKey.indexOf(".");
                int endIndex = entryKey.lastIndexOf(".");
                String sequence;
                sequence = entryKey.substring(endIndex + 1);

                //使用 key 中的 sequence 信息来保证聚合的信息是一个配置组的
                String sequenceKey = key + "." + sequence;
                Map<String, String> valueHashMap = allMap.get(sequenceKey);
                if (valueHashMap == null) {
                    valueHashMap = new HashMap<>();
                    allMap.put(sequenceKey, valueHashMap);
                }

                String substring = entryKey.substring(startIndex + 1, endIndex);
                String value = (String) entry.getValue();
                valueHashMap.put(substring, value);
            }
        }
        return allMap;
    }

    Map<String, Map<String, String>> getSortedMapProperties(String key) {
        Map<String, Map<String, String>> allMapProperties = getAllMapProperties(key);
        Map<String, Map<String, String>> sortedMap = new TreeMap<>(allMapProperties);
        return sortedMap;
    }

    Map<String, Map<String, String>> getSortedMapProperties(String key, String sortKey) {
        Map<String, Map<String, String>> allMapProperties = getAllMapProperties(key);

        Comparator comparator = new MapValueComparator(allMapProperties, sortKey);
        Map<String, Map<String, String>> sortedMap = new TreeMap<>(comparator);
        sortedMap.putAll(allMapProperties);
        return sortedMap;
    }

}
