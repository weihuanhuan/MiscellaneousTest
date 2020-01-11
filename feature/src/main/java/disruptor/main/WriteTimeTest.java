package disruptor.main;

import disruptor.async.AsyncWriter;
import disruptor.core.Writer;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by JasonFitch on 1/10/2020.
 */
public class WriteTimeTest {

    public static void main(String[] args) throws InterruptedException, IOException {
        String userDir = System.getProperty("user.dir");
        String logDir = userDir + "/logs";
        System.out.println(logDir);

        int counts = 5;
        while (counts-- > 0) {
            System.out.println("############################");
            //threadCount  有影响，线程越多，同步的越慢
            //loops        基本无影响，
            //length       很大影响，数据越大，同步的越慢
            testWriterTime(3 * 1, 1 * 5, 3 * 1000 * 1000, 1 * 96, logDir);
        }
    }

    private static void testWriterTime(int interval, int threadCount, int loops, int length, String logDir) throws InterruptedException {
        testAsyncWriterTime(threadCount, loops, length, logDir);
        TimeUnit.SECONDS.sleep(interval);

        testSyncWriterTime(threadCount, loops, length, logDir);
        TimeUnit.SECONDS.sleep(interval);
    }

    private static void testAsyncWriterTime(int threadCount, int loops, int length, String logDir) throws InterruptedException {
        File file = new File(logDir + "/log-writerAsync.txt");
        file.delete();

        Writer writer = new AsyncWriter(file);
        long time = executeWriter(threadCount, loops, length, writer);
        System.out.println(time);
    }

    private static void testSyncWriterTime(int threadCount, int loops, int length, String logDir) throws InterruptedException {
        File file = new File(logDir + "/log-writerSync.txt");
        file.delete();

        Writer writer = new Writer(file);
        long time = executeWriter(threadCount, loops, length, writer);
        System.out.println(time);
    }


    private static long executeWriter(int threadCount, int loops, int length, Writer writer) throws InterruptedException {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                CharArrayWriter charArrayWriter;
                try {
                    charArrayWriter = generateData(length);
                    writer.writerMessage(charArrayWriter);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        long time = executeCommon(threadCount, loops, runnable);
        writer.close();
        return time;
    }

    private static long executeCommon(int threadCount, int loops, Runnable runnable) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        ((ThreadPoolExecutor) executorService).prestartAllCoreThreads();

        long start = System.currentTimeMillis();
        while (loops-- > 0) {
            executorService.submit(runnable);
        }
        executorService.shutdown();
        executorService.awaitTermination(1000, TimeUnit.SECONDS);
        long end = System.currentTimeMillis();
        return end - start;
    }

    private static CharArrayWriter generateData(int length) throws IOException {
        Random random = new Random(0);

        byte[] bytes = new byte[length];
        random.nextBytes(bytes);

        CharArrayWriter charArrayWriter = new CharArrayWriter(length);

        String str = new String(bytes, StandardCharsets.ISO_8859_1);
        charArrayWriter.write(str);

        return charArrayWriter;
    }
}
