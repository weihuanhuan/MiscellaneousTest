package EqualsTest;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by JasonFitch on 3/6/2018.
 */
public class NullEqualsTest {
    public static void main(String[] args) {

        String nullStr = null;
        String str = "str";

        System.out.println(str.equals(nullStr)); //false

        try {
            System.out.println(nullStr.equals(str)); //NullPointerException
            //注意可以将可能为null的String放入equals()里面来间接避免判断null。

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
