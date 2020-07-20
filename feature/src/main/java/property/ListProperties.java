package property;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * Created by JasonFitch on 7/20/2020.
 */
public class ListProperties extends Properties {

    List<String> getListProperties(String key) {
        List<String> result = new ArrayList<>();
        for (Map.Entry<Object, Object> entry : this.entrySet()) {
            if (((String) entry.getKey()).matches("^" + Pattern.quote(key) + "\\.\\d+$")) {
                result.add((String) entry.getValue());
            }
        }
        return result;
    }

}
