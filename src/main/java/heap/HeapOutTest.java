package heap;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * Created by JasonFitch on 4/29/2019.
 */
public class HeapOutTest {

    private static Logger logger = Logger.getLogger(HeapOutTest.class.getName());

    public static void main(String[] args) {

        //JF new Throwable 时其调用堆栈并不是立即填充的。
        Throwable throwable = new Throwable();
        //当调用如下需要使用栈帧信息时的方法时才会正真的填充。
        throwable.getStackTrace();
        throwable.printStackTrace();

        HeapOutTest heapOutTest = new HeapOutTest();

        AtomicInteger precounter = new AtomicInteger(0);
        AtomicInteger postcounter = new AtomicInteger(0);

        int amount = 1000;
        try {
            heapOutTest.Out(amount, precounter, postcounter);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        logger.info(String.valueOf(amount) + " --- " + precounter + " --- " + postcounter);

        amount = 10;
        try {
            heapOutTest.Out(amount, precounter, postcounter);
        } catch (Throwable t) {
            logger.info(t.toString());
        }
        logger.info(String.valueOf(amount) + " --- " + precounter + " --- " + postcounter);

    }

    private void Out(int amount, AtomicInteger precounter, AtomicInteger postcounter) {
        List<BigObject> bigObjectList = new LinkedList<>();
        while ((amount--) > 0) {
            precounter.incrementAndGet();
            bigObjectList.add(new BigObject());
            postcounter.incrementAndGet();
        }
    }

}


