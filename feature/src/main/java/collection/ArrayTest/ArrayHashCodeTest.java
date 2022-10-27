package collection.ArrayTest;

import java.util.Arrays;

/**
 * Created by JasonFitch on 3/28/2019.
 */
public class ArrayHashCodeTest {

    public static void main(String[] args) {

        int length = 10;
        byte[] bytes = new byte[10];
        //JF 数组hashcode可能会重复，
        // 处理session属性变化时，可将session属性序列化，然后计算出他的hash，通过比较之前保存的属性hash来确定Attribute是否变化了
        // 满足 equals相等，数组内容相同，hashCode相同，数组内容不一定是相同的
        // 这种情况最简单的例子便是 map 中使用 String 作为 key 时的 hash 冲突，
        // 所以分布式的环境中或者唯一性key的要求下，需要使用冲突更小的算法，如MD5，最好是目前没有冲突的算法。
        int hashCode = Arrays.hashCode(bytes);
    }
}
