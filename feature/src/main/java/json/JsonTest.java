package json;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JasonFitch on 11/26/2019.
 */
public class JsonTest {

    public static void main(String[] args) {

        stringToJsonstring();
        System.out.println("####################################");

        listToJson();
        System.out.println("####################################");

        objectListToJson();
        System.out.println("####################################");

        mapToJson();
        System.out.println("####################################");

        mapListToJson();
        System.out.println("####################################");

    }

    private static void stringToJsonstring() {
        JsonTest jsonTest = new JsonTest();

        String str = "test";
        System.out.println(str);

        String json = jsonTest.objectToJson(str);
        System.out.println(json);

        String newStr = jsonTest.jsonToString(json);
        System.out.println(newStr);

        System.out.println("--------------------------");

        String emptyStr = "";
        System.out.println(emptyStr);

        String emptyJson = jsonTest.objectToJson(emptyStr);
        System.out.println(emptyJson);

    }

    private String jsonToString(String jsonStr) {
        Gson gson = new Gson();
        String str = gson.fromJson(jsonStr, new TypeToken<String>() {
        }.getType());
        return str;
    }

    public static void mapToJson() {
        JsonTest jsonTest = new JsonTest();

        Map<String, String> map = new HashMap<>();
        map.put("key-1", "value-1");
        map.put("key-2", "value-2");
        System.out.println(map);

        String json = jsonTest.objectToJson(map);
        System.out.println(json);

        Map<String, String> newMap = jsonTest.jsonToMap(json);
        System.out.println(newMap);

        System.out.println("--------------------------");

        Map<String, String> emptyMap = new HashMap<>();
        System.out.println(emptyMap);

        String emptyJson = jsonTest.objectToJson(emptyMap);
        System.out.println(emptyJson);

    }

    private static void mapListToJson() {
        JsonTest jsonTest = new JsonTest();

        Map<String, List<String>> map = new HashMap<>();

        List<String> list1 = new ArrayList<>();
        list1.add("item1-1");
        list1.add("item1-2");

        List<String> list2 = new ArrayList<>();

        map.put("key-1", list1);
        map.put("key-2", list2);
        System.out.println(map);

        String json = jsonTest.objectToJson(map);
        System.out.println(json);

        Map<String, String> newMap = jsonTest.jsonToMap(json);
        System.out.println(newMap);

        System.out.println("--------------------------");

        Map<String, String> emptyMap = new HashMap<>();
        System.out.println(emptyMap);

        String emptyJson = jsonTest.objectToJson(emptyMap);
        System.out.println(emptyJson);


    }

    private <T> Map<String, T> jsonToMap(String jsonStr) {
        Gson gson = new Gson();
        Map<String, T> map = gson.fromJson(jsonStr, new TypeToken<Map<String, T>>() {
        }.getType());
        return map;
    }

    public static void listToJson() {

        JsonTest jsonTest = new JsonTest();

        List<String> stringList = new ArrayList<>();
        stringList.add("test-1");
        stringList.add("test-2");
        System.out.println(stringList);

        String json = jsonTest.objectToJson(stringList);
        System.out.println(json);

        List<Info> list = jsonTest.jsonToList(json);
        System.out.println(list);

        System.out.println("--------------------------");

        List<String> emptyList = new ArrayList<>();
        System.out.println(emptyList);

        String emptyJson = jsonTest.objectToJson(emptyList);
        System.out.println(emptyJson);

    }

    public static void objectListToJson() {
        JsonTest jsonTest = new JsonTest();

        List<Info> infoList = new ArrayList<>();
        infoList.add(new Info());
        infoList.add(new Info());
        System.out.println(infoList);

        String json = jsonTest.objectToJson(infoList);
        System.out.println(json);

        List<Info> list = jsonTest.jsonToList(json);
        System.out.println(list);

        System.out.println("--------------------------");

        List<Info> emptyList = new ArrayList<>();
        System.out.println(emptyList);

        String emptyJson = jsonTest.objectToJson(emptyList);
        System.out.println(emptyJson);
    }

    public <T> List<T> jsonToList(String jsonStr) {
        Gson gson = new Gson();
        List<T> infoList = gson.fromJson(jsonStr, new TypeToken<List<T>>() {
        }.getType());
        return infoList;
    }

    public String objectToJson(Object object) {
        Gson gson = new Gson();
        String jsonStr = gson.toJson(object);
        return jsonStr;
    }

}
