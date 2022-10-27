package concurrency.ThreadPoolExecutorTest.scheduler;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class NanoTImeTest {

    private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static void main(String[] args) throws InterruptedException {

        int plusInSecond = 10;

        long oldCurrentTime = System.currentTimeMillis();
        long oldNanoTime = System.nanoTime();

        Instant instant = Instant.ofEpochMilli(oldCurrentTime);
        Instant plusSeconds = instant.plusSeconds(plusInSecond);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(PATTERN);
        Date from = Date.from(plusSeconds);
        String newFormat = simpleDateFormat.format(from);
        System.out.println("set time: " + newFormat);

        //需要以管理员身份来运行才能正确的调用到 windows 的 time 命令
        try {
            String[] s = newFormat.split(" ");
            String command = String.format("cmd /c time %s", s[1]);
            System.out.println("command:" + command);
            Process exec = Runtime.getRuntime().exec(command);
            int i = exec.waitFor();
            System.out.println("exec:" + i);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //延时睡眠来加大 diff 之间的数值，便于观察。
        TimeUnit.SECONDS.sleep(3);

        //java.lang.System.nanoTime 获取的时间是独立于 操作系统时间，可以相当准确的用来计算逝去的时间，用以可靠的处理时间间隔的任务
        long newNanoTime = System.nanoTime();
        long diffNano = newNanoTime - oldNanoTime;
        System.out.println("oldNanoTime:" + TimeUnit.NANOSECONDS.toSeconds(oldNanoTime));
        System.out.println("newNanoTime:" + TimeUnit.NANOSECONDS.toSeconds(newNanoTime));
        System.out.println("diff in second with java.lang.System.nanoTime:" + TimeUnit.NANOSECONDS.toSeconds(diffNano));

        //java.lang.System.currentTimeMillis 获取的时间依赖于 操作系统时间，可以用来处理对绝对时间精度要求不高的，依赖系统时间相关的任务
        long newCurrentTime = System.currentTimeMillis();
        long diffCurrent = newCurrentTime - oldCurrentTime;
        System.out.println("oldCurrentTime:" + TimeUnit.MILLISECONDS.toSeconds(oldCurrentTime));
        System.out.println("newCurrentTime:" + TimeUnit.MILLISECONDS.toSeconds(newCurrentTime));
        System.out.println("diff in second with java.lang.System.currentTimeMillis:" + TimeUnit.MILLISECONDS.toSeconds(diffCurrent));
    }

}

