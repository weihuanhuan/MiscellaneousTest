package property;

import java.util.Comparator;
import java.util.Map;

/**
 * Created by JasonFitch on 7/20/2020.
 */
public class MapValueComparator implements Comparator<String> {

    private Map<String, Map<String, String>> unsortedMap;
    private String sortKey;

    public MapValueComparator(Map<String, Map<String, String>> unsortedMap, String sortKey) {
        this.unsortedMap = unsortedMap;
        this.sortKey = sortKey;
    }

    @Override
    public int compare(String o1, String o2) {
        Map<String, String> map1 = unsortedMap.get(o1);
        Map<String, String> map2 = unsortedMap.get(o2);
        String order1 = map1.get(sortKey);
        String order2 = map2.get(sortKey);
        return order1.compareTo(order2);
    }
}
