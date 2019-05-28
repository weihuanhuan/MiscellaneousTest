package EqualsTest;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by JasonFitch on 5/28/2019.
 */
public class ArrayEqualsTest {

    public static void main(String[] args) {
        Object[] objectsA = new Object[]{"a", "b"};
        Object[] objectsB = new Object[]{"a", "b"};

        ConcurrentMap concurrentMap = new ConcurrentHashMap();
        concurrentMap.putIfAbsent(objectsA, "A");

        //这里oA为值A，而oB为null，原因如下所述
        Object oA = concurrentMap.get(objectsA);
        System.out.println(oA);
        Object oB = concurrentMap.get(objectsB);
        System.out.println(oB);


        //对于Object来说 equals 和 == 是相同的，都是基于地址判断相等性的。
        System.out.println(objectsA == objectsB);
        System.out.println(objectsA.equals(objectsB));

        //而Arrays.equals是基于数组中的每一个元素来判断相等的，注意其依赖于元素本身的equals方法。
        System.out.println(Arrays.equals(objectsA, objectsB));
    }
}
