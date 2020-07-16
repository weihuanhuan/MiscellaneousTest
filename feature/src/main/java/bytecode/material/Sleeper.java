package bytecode.material;

/**
 * Created by JasonFitch on 7/16/2020.
 */
public class Sleeper {

    public void sleep() throws InterruptedException {
        long start = System.currentTimeMillis();
        Thread.sleep(1000);
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }

    public void anotherSleep() throws InterruptedException {
        long start = System.currentTimeMillis();
        Thread.sleep(2000);
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }

}
