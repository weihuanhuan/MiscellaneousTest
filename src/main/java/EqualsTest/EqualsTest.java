package EqualsTest;

/**
 * Created by JasonFitch on 3/6/2018.
 */
public class EqualsTest
{
    public static void main(String[] args)
    {
        String nullStr=null;
        String str="str";

        System.out.println(str.equals(nullStr)); //false
        System.out.println(nullStr.equals(str)); //NullPointerException

        //注意可以将可能为null的String放入equals()里面来间接避免判断null。

    }
}
