package jvm.ThrowException;

/**
 * Created by JasonFitch on 10/28/2021.
 */
public class FinallySwallowCatchTest {

    public static void main(String[] args) throws Exception {

        //将 err 设置为 out 使得输出流是同一个流，来保证 exception 栈 和 提示信息的打印是按照代码执行的顺序来输出的
        System.setErr(System.out);

        System.out.println("#################### swallowCatchException ####################");

        try {
            FinallySwallowCatchTest.swallowCatchException();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("#################### keepCatchException ####################");

        try {
            FinallySwallowCatchTest.keepCatchException();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void swallowCatchException() throws Exception {
        try {
            throw new Exception("direct exception 1");
        } catch (Exception e) {
            //这里的异常如果没有进行 log 操作，可能会在随后的 finally 中被吞掉，使得调用者无法获取到真正导致异常的直接原因。
            //throw direct exception 1
            throw e;
        } finally {
            //在 finally 中的异常将会覆盖上面的 catch 中的真实异常，使得调用者看见的异常变成了 finally 中的间接原因。
            //throw indirect exception 1
            throw new Exception("indirect exception 1");
        }
    }

    public static void keepCatchException() throws Exception {
        try {
            throw new Exception("direct exception 2");
        } catch (Exception e) {
            //直接将真实的异常进行 throw ，以此来通知调用者调用错误的真实原因，并将调用错误的处理职责一并转移给调用者。
            //throw direct exception 2
            throw e;
        } finally {
            //通过在 finally 中在次 catch 的所有的异常将会抑制在这里的 finally 中潜在的异常发生，从而保留上面的 catch 中的真实异常。
            //suppress indirect exception 2
            try {
                throw new Exception("indirect exception 2");
            } catch (Throwable throwable) {
                //这里是对异常的父类 Throwable 进行 catch ，以防止意外异常的覆盖发生。
                //to log this exception for debug
            }
        }
    }

}
