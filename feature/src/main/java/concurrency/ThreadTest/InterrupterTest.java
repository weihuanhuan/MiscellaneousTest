package concurrency.ThreadTest;

import java.util.concurrent.TimeUnit;

/**
 * Created by JasonFitch on 4/3/2019.
 */
public class InterrupterTest {
    public static void main(String[] args) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                int i = 10;

                while (true) {

                    Thread thread = Thread.currentThread();
                    boolean interrupted = thread.isInterrupted();
                    System.out.println("run while interrupted=" + interrupted);
                    if (interrupted) {
                        // 利用中断信息检测该线程是否应该结束执行，如果不处理的话线程将在保留中断信息的状态下继续运行。

                        //处理1：依据中断信息正常的结束该线程。
//                        break;

                        //处理2：利用清空中断的方法，使得线程可以在清空中断信息的状态，从而继续正常运行。
                        //注意清空中断的 interrupted() 方法是 Thread 的静态方法, 他只能清空当前线程的中断信息，直接清空中断状态。
                        Thread.interrupted();

                        //或者利用线程处于中断状态下时，调用某些检测到中断状态会立马抛出 InterruptedException 的方法来间接情况中断状态
//                        try {
//                            TimeUnit.MILLISECONDS.sleep(1);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
                    }
                    i = doSomeThing(i);

                }

            }

            private int doSomeThing(int i) {
                try {

                    Thread thread = Thread.currentThread();
                    boolean interrupted = thread.isInterrupted();
                    System.out.println("doSomeThing try interrupted=" + interrupted);
                    TimeUnit.MILLISECONDS.sleep(1000);

                } catch (InterruptedException e) {
                    Thread thread = Thread.currentThread();
                    boolean interrupted = thread.isInterrupted();
                    System.out.println("doSomeThing catch interrupted=" + interrupted);
                    //JF 输出的线程是 taskThread，
                    //   interrupt 并不会使线程死亡，线程依旧可以运行
                    //   同时线程可以选择发生中断时处理或者不处理，
                    //   如果不处理，此时线程继续执行，同时其中断信息在catch InterruptedException 后变重置消失了。
                    System.out.println(thread);

                    // 如果暂时不需要处理中断信息，但是需要在其他时刻使用改状态，那么必须调用 interrupt()方法，将线程中断信息恢复
                    // 比如这里利用中断信息在 run() 中正常结束一个死循环。
                    thread.interrupt();

                    // 而异常如果不处理会使得线程直接死亡，这样子就不会再执行了，当然如果对异常进行catch后也不会导致 taskThread 线程死亡
//                    throw new RuntimeException();

                }
                return ++i;
            }
        }, "taskThread");


        t.start();

        // 延迟，使之在 doSomeThing()的sleep过程中触发中断，
        // 否则太早 interrupt 可能在 doSomeThing()方法中无法catch住，导致在 run() 中的 while 过程中通过 if 判断来处理这个中断了。
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        t.interrupt();

        // 延迟，确定 main 线程的状态
        try {
            TimeUnit.MILLISECONDS.sleep(1000 * 2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Thread thread = Thread.currentThread();
        System.out.println("######################" + thread + "###################");

    }


}
