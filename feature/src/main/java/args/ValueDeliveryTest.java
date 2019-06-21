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

    private static void test(List<String> list) {

        list = new ArrayList<>();
        list.add("asd");


    }


}
