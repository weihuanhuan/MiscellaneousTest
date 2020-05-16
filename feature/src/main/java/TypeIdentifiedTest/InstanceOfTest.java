package TypeIdentifiedTest;

/**
 * Created by JasonFitch on 4/16/2019.
 */
public class InstanceOfTest {


    public static void main(String[] args) {

        //JF 对于值为null的对象可以直接判断其类型
        System.out.println(null instanceof  Exception);


        //JF 即使显式的指定对象的类型，然后赋值为 null , 也不会通过 instanceof 运算符的测试
        String nullString = null;
        if (nullString instanceof String) {
            System.out.println("nullString instanceof String");
        } else {
            System.out.println("nullString isn't instanceof String");
        }

    }
}
