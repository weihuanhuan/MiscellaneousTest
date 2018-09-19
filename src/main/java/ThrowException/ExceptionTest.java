package ThrowException;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by JasonFitch on 2/6/2018.
 */
public class ExceptionTest {

    public static void main(String[] args) {
        System.out.println(ClassLoader.getSystemClassLoader());

        try {
            try {
                throw new Error("erorr");
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
//            Error 其实也可以捕获。

            try {
                throw new Exception("exception");
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                throw new RuntimeException("runtime");
            } catch (RuntimeException e) {
                e.printStackTrace();
            }

            ExceptionTest exceptionTest = new ExceptionTest();
            exceptionTest.throwException();

        } catch (IOException e) {
//            虽然方法没有真实的抛出异常，但是方法声明了异常所以也需要catch，即使什么也catch不到。
            //  捕获异常后可以不处理
        } finally {
            System.out.println("------------");
        }

    }


    public void throwException() throws IOException {
        IOException ioException = new IOException("ioexception");
//        throw ioException;
//        不抛出异常，函数也可以有 throws 子句
    }
}
