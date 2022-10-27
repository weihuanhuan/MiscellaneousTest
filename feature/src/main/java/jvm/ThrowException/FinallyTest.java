package jvm.ThrowException;

/**
 * Created by JasonFitch on 5/20/2019.
 */
public class FinallyTest {


    public static void main(String[] args) {

        try {
            catchFinally();
        } catch (Exception e) {
            e.printStackTrace();
        }

        int i = returnFinally();
        System.out.println(i);

    }


    public static void catchFinally() throws Exception {
        try {
            throw new Exception("try test");
        } catch (Exception e) {
            throw new Exception("catch test", e);
        } finally {
            //catch 中的异常 finally 中的语句也会执行
            System.out.println("finally test");
        }

    }

    public static int returnFinally() {
        try {
            System.out.println("try 1");
            return 1;
        } finally {
            //对于return 来说，finally的return会覆盖其他部分的return值。
            System.out.println("finally 2");
            return 2;
        }
    }


}
