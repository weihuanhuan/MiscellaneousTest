package basic.operator;

/**
 * Created by JasonFitch on 1/26/2019.
 */
public class MainTest {

    public static void main(String[] args) {
        add1();
        add2();
        add3();
        add4();
        add5();
    }

    private static void add1() {
        System.out.println("add1");
        int i = 1;
        System.out.println(i++ + i++);
        System.out.println(i);
//        3 = 1 + 2
//        3 = ++ ++
//        load inc load inc add store
//          1   2   2    3   3    3
    }

    private static void add2() {
        System.out.println("add2");
        int i = 1;
        System.out.println(i++ + ++i);
        System.out.println(i);
//        4 = 1 + 3
//        3 = ++ ++
//        load inc inc load add store
//          1   2   3    3   4    3
    }

    private static void add3() {
        System.out.println("add3");
        int i = 1;
        System.out.println(i + +(++i));
        System.out.println(i);
//        3 = 1 + 2
//        2 = ++
    }

    private static void add4() {
        System.out.println("add4");
        int i = 1;
        System.out.println(i + +(+ +i));
        System.out.println(i);
//        2 = 1 + 1
//        1 = 1
    }

    private static void add5() {
        System.out.println("add5");
        int i = 1;
        //JF 注意 + 赋值运算符 和 ++ 自增运算符是不同的运算符
        System.out.println(+ +i);
        System.out.println(++i);
        i = 1;
        //JF Java会忽略调多余的 + 赋值运算符，所以多个 + 赋值运算符出现，等同于一个。
        System.out.println(i + + + +i + + + +i);
        System.out.println(i++);
        //1
        //2
        //3
        //1
    }

}
