package json.jsonp;

import java.util.HashMap;
import java.util.Map;
import json.jsonp.bean.ConnectionInfo;
import json.jsonp.bean.LoadBalanceInfo;
import json.jsonp.helper.JsonPHelper;

import javax.json.JsonValue;
import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class JsonPMain {

    public static void main(String[] args) throws IllegalAccessException, IntrospectionException, InvocationTargetException, IOException, ClassNotFoundException, InstantiationException {
        testNormalInstance();
        System.out.println();

        testNullFieldInstance();
        System.out.println();

        testNullInstance();
        System.out.println();

        testInstanceMap();
        System.out.println();
    }

    private static void testNormalInstance() throws IllegalAccessException, IntrospectionException, InvocationTargetException, IOException, ClassNotFoundException, InstantiationException {
        Object normalInstance = createNormalInstance();
        test(normalInstance);
    }

    private static Object createNormalInstance() {
        LoadBalanceInfo loadBalanceInfo = new LoadBalanceInfo();
        loadBalanceInfo.setInstanceName("ins1");
        loadBalanceInfo.setWeight(100);
        loadBalanceInfo.setChar('e');
        loadBalanceInfo.setByte((byte) 23);
        ConnectionInfo http = new ConnectionInfo("http", 1900);
        loadBalanceInfo.setHttpConnectionInfo(http);
        ConnectionInfo spark = new ConnectionInfo("spark", 3000);
        loadBalanceInfo.setSparkConnectionInfo(spark);
        return loadBalanceInfo;
    }

    private static void testNullFieldInstance() throws IllegalAccessException, IntrospectionException, InvocationTargetException, IOException, ClassNotFoundException, InstantiationException {
        Object nullFieldInstance = createNullFieldInstance();
        test(nullFieldInstance);
    }

    private static Object createNullFieldInstance() {
        LoadBalanceInfo loadBalanceInfo = new LoadBalanceInfo();
        loadBalanceInfo.setHttpConnectionInfo(null);
        ConnectionInfo spark = new ConnectionInfo("spark", 3000);
        loadBalanceInfo.setSparkConnectionInfo(spark);
        return loadBalanceInfo;
    }

    private static void testNullInstance() throws IllegalAccessException, IntrospectionException, InvocationTargetException, InstantiationException, IOException, ClassNotFoundException {
        Object nullInstance = createNullInstance();
        test(nullInstance);
    }

    private static Object createNullInstance() {
        return null;
    }

    private static void test(Object instanceOld) throws IllegalAccessException, IntrospectionException, InvocationTargetException, InstantiationException, IOException, ClassNotFoundException {
        JsonValue jsonValue = JsonPHelper.buildJsonValue(instanceOld);
        String jsonString = JsonPHelper.generateJsonString(jsonValue);
        System.out.println(jsonString);

        JsonValue jsonValueNew = JsonPHelper.parseJsonString(jsonString);
        Object instanceNew = JsonPHelper.buildInstance(jsonValueNew, LoadBalanceInfo.class);

        System.out.println(instanceOld);
        System.out.println(instanceNew);
        if (instanceOld != null) {
            boolean isEqual = instanceOld.equals(instanceNew);
            System.out.println(String.format("instanceOld.equals(instanceNew):%s", isEqual));
        } else {
            boolean isEqual = instanceOld == instanceNew;
            System.out.println(String.format("instanceOld == instanceNew:%s", isEqual));
        }
    }

    private static void testInstanceMap() throws IllegalAccessException, IntrospectionException, InstantiationException, IOException, InvocationTargetException, ClassNotFoundException {
        Map<String, LoadBalanceInfo> instanceMap = createInstanceMap();
        testMap(instanceMap);
    }

    private static Map<String, LoadBalanceInfo> createInstanceMap() throws IllegalAccessException, IntrospectionException, InstantiationException, ClassNotFoundException, InvocationTargetException, IOException {
        Map<String, LoadBalanceInfo> map = new HashMap<>();
        LoadBalanceInfo normalInstance = (LoadBalanceInfo) createNormalInstance();
        map.put("ins1", normalInstance);
        LoadBalanceInfo nullFieldInstance = (LoadBalanceInfo) createNullFieldInstance();
        map.put("ins2-dummy", nullFieldInstance);
        LoadBalanceInfo nullInstance = (LoadBalanceInfo) createNullFieldInstance();
        map.put("ins3-dummy", nullInstance);
        return map;
    }

    private static <V> void testMap(Map<String, V> mapOld) throws IllegalAccessException, IntrospectionException, InvocationTargetException, ClassNotFoundException, IOException, InstantiationException {
        JsonValue jsonValue = JsonPHelper.buildMapJsonValue(mapOld);
        String jsonString = JsonPHelper.generateJsonString(jsonValue);
        System.out.println(jsonString);

        JsonValue jsonValueNew = JsonPHelper.parseJsonString(jsonString);
        Object mapNew = JsonPHelper.buildInstanceMap(jsonValueNew, LoadBalanceInfo.class);

        System.out.println(mapOld);
        System.out.println(mapNew);
        if (mapOld != null) {
            boolean isEqual = mapOld.equals(mapNew);
            System.out.println(String.format("mapOld.equals(mapNew):%s", isEqual));
        } else {
            boolean isEqual = mapOld == mapNew;
            System.out.println(String.format("mapOld == mapNew:%s", isEqual));
        }
    }

}
