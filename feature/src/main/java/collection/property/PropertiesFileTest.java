package collection.property;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by JasonFitch on 7/20/2020.
 */
public class PropertiesFileTest {

    public static void main(String[] args) throws IOException {
        String userDir = System.getProperty("user.dir");
        String subDir = "feature" + "/src/main/java";
        File propertiesFile = new File(userDir, subDir + "/" + "collection/property/test.properties");
        System.out.println(propertiesFile.getAbsolutePath());

        duplicateKey(propertiesFile);

        listKey(propertiesFile);

        mapKey(propertiesFile);
    }

    public static void duplicateKey(File propertiesFile) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(propertiesFile));
        System.out.println(properties);
    }

    private static void listKey(File propertiesFile) throws IOException {
        ListProperties properties = new ListProperties();
        properties.load(new FileInputStream(propertiesFile));
        System.out.println(properties.getListProperties("class"));
        System.out.println(properties.getListProperties("type"));
        System.out.println(properties.getListProperties("target"));
    }

    private static void mapKey(File propertiesFile) throws IOException {
        MapProperties properties = new MapProperties();
        properties.load(new FileInputStream(propertiesFile));
        //直接遍历 key 可能受 hashtable 中元素迭代的顺序影响，导致产生的 map 不是一个组中的结果
        System.out.println(properties.getMapProperties("trans"));
        //所以我们要使用 key 中的 sequence 来确保某一组配置最终聚合在一个 map 里面。
        System.out.println(properties.getAllMapProperties("trans"));
        //默认情况下 TreeMap 使用 key 来排序
        //即，使用了 properties 中的 主字段 trans 和其组标识符的 sequence 序号来排序
        System.out.println(properties.getSortedMapProperties("trans"));
        //通过自定义的 Comparator 我们实现按照值来排序
        //这里的值是一个 HashMap,我们进一步使用此 Map 中的 key order 来排序。
        //即，使用了 properties 中的某个配置组的子字段 order 来排序
        System.out.println(properties.getSortedMapProperties("trans", "order"));
    }

}
