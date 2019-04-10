package ThreadTest;

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
                    i = doSomeThing(i);
                }
            }

            private int doSomeThing(int i) {
                return ++i;
            }
        });


        t.start();

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        t.interrupt();

        System.out.println("ok");

    }


}
