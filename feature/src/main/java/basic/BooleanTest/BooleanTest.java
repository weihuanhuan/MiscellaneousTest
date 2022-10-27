package basic.BooleanTest;

import java.util.Objects;

/**
 * Created by JasonFitch on 5/29/2020.
 */
public class BooleanTest {

    public static void main(String[] args) {

        //相同的对象和 bBoolean
        Boolean aBoolean = Boolean.valueOf(true);
        System.out.println(aBoolean);

        //相同的对象和 aBoolean
        Boolean bBoolean = Boolean.valueOf(true);
        System.out.println(bBoolean);

        boolean parseBoolean = Boolean.parseBoolean("true");
        System.out.println(parseBoolean);

        String toString = Boolean.toString(true);
        System.out.println(toString);

        boolean logicalXor = Boolean.logicalXor(aBoolean, bBoolean);
        System.out.println(logicalXor);

        //boolean 类型的系统属性
        boolean b = Boolean.getBoolean("java.security.manager");
        System.out.println(b);

        System.out.println("############################################");
        boolean booleanValue = aBoolean.booleanValue();
        System.out.println(booleanValue);

        int compareTo = aBoolean.compareTo(bBoolean);
        System.out.println(compareTo);

        //Boolean 对象没有 set 方法
//        aBoolean.setBooleanValue()

    }
}
