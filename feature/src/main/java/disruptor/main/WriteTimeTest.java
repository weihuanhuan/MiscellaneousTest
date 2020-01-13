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
            System.out.println("######### " + counts + " ###########");
            //写入数据操作需要竞争的锁
            //             对于异步 com.lmax.disruptor.AbstractSequencer 的锁
            //                  和 java.io.Writer.lock 的锁，但是由于使用了disruptor，将写入事件只交付给一个专职线程来完成，故这个锁实际无需竞争。
            //             对于同步 disruptor.core.Writer 的锁，
            //                  和 java.io.Writer.lock的锁，这个锁的获取在 Writer 之后获取，所以实际上也可以看成不需要的，
            //             使用 disruptor 相比于原先，主要的区别在于，
            //                  work线程竞争锁的时候由写入 数据时 竞争 Writer，转变为 写入前 竞争 AbstractSequencer 入队时的锁。

            //但是也带来一个问题，就是对于那些循环使用的数据，
            //             比如 web 服务器的 request 和 response，由于他们是可回收利用的，那么对于他们属性的使用，
            //             要么 在同步线程中处理，随后这些属性被回收掉。
            //             要么 复制一份 在异步线程中处理，这里的复制可能是一个昂贵的开销。

// 参考 org.apache.coyote.Request.recycle()             和 org.apache.coyote.Response.recycle()
//     (low-level  面向 protocol 处理)
//
//     org.apache.catalina.connector.Request.recycle() 和 org.apache.catalina.connector.Response.recycle()
//     (high-level 面向 servlets 处理, 包装了 low-level 的对象)
//
// org.apache.catalina.connector.CoyoteAdapter.service()
//            @Override
//            public void service(org.apache.coyote.Request req, org.apache.coyote.Response res)
//            throws Exception {
//
//                Request request = (Request) req.getNote(ADAPTER_NOTES);
//                Response response = (Response) res.getNote(ADAPTER_NOTES);
//
//                if (request == null) {
//                     Create objects
//                    request = connector.createRequest();
//                    request.setCoyoteRequest(req);
//                    response = connector.createResponse();
//                    response.setCoyoteResponse(res);
//
//                     Link objects
//                    request.setResponse(response);
//                    response.setRequest(request);
//
//                     Set as notes
//                    req.setNote(ADAPTER_NOTES, request);
//                    res.setNote(ADAPTER_NOTES, response);
//
//                     Set query string encoding
//                    req.getParameters().setQueryStringCharset(connector.getURICharset());
//                }
//            ignore other

            //interval     并没有什么用的属性，让系统休息一会儿。

            //threadCount  对于异步，线程越多速度越慢。(增加了异步队列入队时锁的竞争压力)
            //             对于同步，基本没影响，
            //loops        对于异步和同步来说，都是花费的时间按照相似的比例倍数扩大，故次数基本不影响。
            //length       对于异步，花费的时间和数据量增大的倍数成正比。
            //             对于同步，则是时间增大的倍数大于异步的增大倍数，故数据量大的时候效果比异步的要差。(为什么呢？增加了写数据时竞争压力？)
            testWriterTime(1 * 1, 1 * 10, 5 * 1000 * 1000, 1 * 96, logDir);
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
        System.out.println("asyncTime=" + time);

        //对于异步的写入要注意，如果异步线程还没写完就查看写入的信息条数，会导致一些消息来不及写入，所以这里的 counter 值要小于同步的场景。
        System.out.println("asyncCountDoing=" + writer.getCounter());

        //对于异步的写入起，这个方法可以等待异步队列中的事件都执行完毕。
        writer.close();

        //此时再次查看消息的写入情况，发现所有的消息都已经写入了。
        System.out.println("asyncCountFinish=" + writer.getCounter());
    }

    private static void testSyncWriterTime(int threadCount, int loops, int length, String logDir) throws InterruptedException {
        File file = new File(logDir + "/log-writerSync.txt");
        file.delete();

        Writer writer = new Writer(file);
        long time = executeWriter(threadCount, loops, length, writer);
        System.out.println("syncTime=" + time);

        //而对于同步的写入情况下，当提交写任务的线程全都执行完毕后，所有的消息其实也就是完成写入了，所以这里的 counter 值等于实际的写入数量。
        System.out.println("syncCountFinish=" + writer.getCounter());
        writer.close();
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

        return executeCommon(threadCount, loops, runnable);
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
