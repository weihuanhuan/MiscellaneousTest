package ThreadTest;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.omg.CORBA.TIMEOUT;

/**
 * Created by JasonFitch on 9/5/2018.
 */
public class ThreadTest
{

    public static void main(String[] args)
    {
        String tName1 = "thread 1";
        Thread t1 = new Runner(tName1);
        t1.start();

        String tName2 = "thread 2";
        Thread t2 = new Runner(tName2);
        t2.start();


        try
        {
            System.in.read();

        } catch (IOException e)
        {
            e.printStackTrace();
        }

    }
}

class Runner extends Thread
{

    private static final int WAIT_TIME_SECOND = 1000;

    public Runner(String tName1)
    {
        super(tName1);
    }

    @Override
    public void run()
    {
        try
        {
            while (true)
            {
                waitForOutput();
            }

        } catch (InterruptedException e)
        {
            e.printStackTrace();
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            synchronized(this)
            {
                notifyAll();
            }
        }
    }

    public void waitForOutput() throws InterruptedException
    {
        System.out.println(this.getName());
        synchronized (this)
        {
            this.wait(WAIT_TIME_SECOND * 3);


//                TimeUnit.SECONDS.wait(3);
//                Exception in thread "thread 1" Exception in thread "thread 2" java.lang.IllegalMonitorStateException
//                at java.lang.Object.wait(Native Method)
//                at ThreadTest.Runner.waitForOutput(ThreadTest.java:63)
//                at ThreadTest.Runner.run(ThreadTest.java:51)
//                java.lang.IllegalMonitorStateException
//                at java.lang.Object.wait(Native Method)
//                at ThreadTest.Runner.waitForOutput(ThreadTest.java:63)
//                at ThreadTest.Runner.run(ThreadTest.java:51)
        }
    }

}