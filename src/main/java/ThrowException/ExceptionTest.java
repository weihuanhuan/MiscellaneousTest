package ThrowException;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by JasonFitch on 2/6/2018.
 */
public class ExceptionTest {

    private SnapshotCallStack snapshot = null;

    public static void main(String[] args) {

        try {
            try {
                throw new Error("erorr");
            } catch (Throwable throwable) {
                System.out.println(throwable);
            }
//            Error 其实也可以捕获。

            try {
                throw new RuntimeException("runtime");
            } catch (Exception e) {
                System.out.println(e);
            }
//           父类异常可以捕获子类异常

//            String str = null;
//            str.length();
//            NullPointerException ,属于运行时异常，是一个不受检查的异常，即使未在throws子句中声明也可以传播。

            ExceptionTest exceptionTest = new ExceptionTest();
            exceptionTest.throwException();

            exceptionTest.fillCallStack();
            try {
                throw exceptionTest.snapshot;
            } catch (SnapshotCallStack snapshotCallStack) {
                snapshotCallStack.printStackTrace();
//                异常栈的轨迹是从调用栈开始直到 new 异常的地方, 和throw 语句抛出的位置无关。
            }

        } catch (IOException e) {
//            虽然方法没有真实的抛出异常，但是方法声明了异常所以也需要catch，即使什么也catch不到。
//            捕获异常后可以不处理
        } finally {
            System.out.println("------------");
        }

    }

    public void throwException() throws IOException {
        IOException ioException = new IOException("ioexception");
//        throw ioException;
//        不抛出异常，函数也可以有 throws 子句
//        而且只是new异常，没有 throw 动作不会导致异常出现。
    }

    public void fillCallStack() {
        snapshot = new SnapshotCallStack();
    }
}

class SnapshotCallStack extends Throwable {
    public final long timestamp = System.currentTimeMillis();
}
