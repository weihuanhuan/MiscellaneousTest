package args;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JasonFitch on 6/21/2019.
 */
public class ValueDeliveryTest {

    public static void main(String[] args) {

        List<String> list = null;
        test(list);
        System.out.println(list.size());

    }

    //JF 注意part1和part2是相等的，
    //   java 是基于值copy传递的，这里复制了新的引用list，只是这个引用和先前的引用指向同一个地址，
    //   随后修改了，新的引用，函数结束，这里可以看见旧的引用并没有进行任何修改所以是空指针异常。
    //part 1
    private static void test(List<String> list) {
        list = new ArrayList<>();
        list.add("asd");
    }

    //   因此part1相当于是下面的形式，list定义了一个本地变量，全程使用的是本地变量而已。
    //part 2
    private static void test() {
        List<String> list = new ArrayList<>();
        list.add("asd");
    }


}
