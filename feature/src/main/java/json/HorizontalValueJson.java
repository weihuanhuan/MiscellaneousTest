package json;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.Map;

/**
 * Created by JasonFitch on 12/3/2019.
 */
public class HorizontalValueJson {

    public static void main(String[] args) {

        StringBuilder sb = new StringBuilder();
        sb.append("{");

        sb.append("\"");
        sb.append("system-log");
        sb.append("\"");
        sb.append(":");
        sb.append("true");
        sb.append(",");

        sb.append("\"");
        sb.append("access-log");
        sb.append("\"");
        sb.append(":");
        sb.append("true");
        sb.append(",");

        sb.append("\"");
        sb.append("dump-type");
        sb.append("\"");
        sb.append(":");
        sb.append("\"");
        sb.append("all");
        sb.append("\"");

        sb.append("}");

        String string = sb.toString();
        System.out.println(string);

        Gson gson = new Gson();
        Map<String, String> map = gson.fromJson(string, new TypeToken<Map<String, String>>() {
        }.getType());

        System.out.println(map);

    }


}
