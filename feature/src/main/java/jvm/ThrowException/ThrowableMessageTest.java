package jvm.ThrowException;

import java.util.Arrays;

/**
 * Created by JasonFitch on 6/18/2019.
 */
public class ThrowableMessageTest {


    public static void main(String[] args) {

        // 注意debug时，可能看不到异常的栈细节，其值为 StackTraceElement[0]，空的栈帧数组
        // 是因为new出throwable时，其默认的栈是 UNASSIGNED_STACK，一个空的栈对象。
        // private static final StackTraceElement[] UNASSIGNED_STACK = new StackTraceElement[0];
        // 在调用 getStackTrace 或者是 printStackTrace 是才会进行填充真是的栈帧数据。
        Throwable throwable = new Throwable();
        StackTraceElement[] stackTrace = throwable.getStackTrace();
        System.out.println(Arrays.deepToString(stackTrace));

        handleException(new IllegalArgumentException("testMessage"));
        System.out.println("--------------------------------------------------");

        //null 作为 String，表示异常描述
        handleException(new IllegalArgumentException((String) null));
        //null 作为 Throwable，表示异常原因
        handleException(new IllegalArgumentException((Throwable) null));
        System.out.println("--------------------------------------------------");

        //表异常原因的对象，其toString的结果将作为异常的描述而出现。
        handleException(new IllegalArgumentException(new Throwable()));
        handleException(new IllegalArgumentException(new Throwable("testThrowable")));
        System.out.println("--------------------------------------------------");

//        构造异常时，如果没指定message，同时指定了 throwable则使用 cause 的 toString作为该异常的描述
//        public Throwable(Throwable cause) {
//            fillInStackTrace();
//            detailMessage = (cause==null ? null : cause.toString());
//            this.cause = cause;
//        }
//        异常的输出包含异常描述
//        public String toString() {
//            String s = getClass().getName();
//            String message = getLocalizedMessage();
//            return (message != null) ? (s + ": " + message) : s;
//        }

        handleException(new IllegalArgumentException("testMessage", new Throwable("testThrowable")));
        System.out.println("--------------------------------------------------");

    }

    public static void handleException(Throwable throwable) {
        try {
            throw throwable;
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }
}
