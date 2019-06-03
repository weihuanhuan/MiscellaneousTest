import java.util.Properties;

/**
 * Created by JasonFitch on 5/31/2019.
 */
public class PropertiesTest {


    public static void main(String[] args) {

        String a1Key = "a1k";
        String a2Key = "a2k";

        String b1Key = "b1k";

        Properties propertiesA = new Properties();
        propertiesA.put(a1Key, "a1v");
        propertiesA.put(a2Key, "a2v");
        System.out.println(propertiesA);

        String a1Value = propertiesA.getProperty(a1Key);
        System.out.println(a1Value);

        String a2Value = (String) propertiesA.get(a2Key);
        System.out.println(a2Value);
        System.out.println("--------------------------------");

        //JF 注意 properties 本身继承自一个hashtable，其相当与map对象，拥有map对象的基本操作方法.
        //   所以使用时应当注意，这些原生的map方法一般不会操作其构造properties，
        //   而其本身实现的部分方法则会考虑 new Properties 时的构造对象，具体见如下示例：
        Properties propertiesB = new Properties(propertiesA);
        Object remove = propertiesB.remove(a1Key);
        System.out.println(remove);

        //JF getProperty会操作其构造 properties的属性，
        //   getproperty会优先查找自己持有的属性，如果没有,结果是null，则再从构造得来的properties中查找
        a1Value = propertiesB.getProperty(a1Key);
        System.out.println(a1Value);

        //JF 注意setProperty方法依旧指考虑其自身，无视其持有的Properties.
        propertiesB.setProperty(b1Key,"b1v");

        //JF get remove 等map相关操作 不会操作其构造properties的属性，只从自身的map中查找相关属性
        a1Value = (String) propertiesB.get(a1Key);
        System.out.println(a1Value);

        System.out.println(propertiesB);

    }


}
