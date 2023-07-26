package SystemTest.process;

import java.io.IOException;
import java.io.PrintStream;

public class LongTermProcess {

    private static final int toMBScale = 8 * 1024;

    public static void main(String[] args) {
        int block = 128;//byte
        int count = toMBScale * 8;//mb
        if (args != null && args.length == 2) {
            block = Integer.parseInt(args[0]);
            count = Integer.parseInt(args[1]);
        }

        //range= 10(0-9) ,offset = 48(0)
        char[] numChars = generateContent(block, 10, 48);
        //range= 26(A-Z) ,offset = 65(A)
        char[] alphaChars = generateContent(block, 26, 65);

        PrintTask stdoutPrintTask = new PrintTask(block, count, System.out, numChars);
        Thread stdoutThread = new Thread(stdoutPrintTask, "PrintTask-System.out");
        //保证主进程退出后， stdout/stderr 不会继续在运行防止 main 退出，进而导致 process 退出。
//        stdoutThread.setDaemon(true);
        stdoutThread.start();

        PrintTask stderrPrintTask = new PrintTask(block, count, System.err, alphaChars);
        Thread stderrThread = new Thread(stderrPrintTask, "PrintTask-System.err");
//        stderrThread.setDaemon(true);
        stderrThread.start();

        //防止主进程退出，使得该进程即使没有打印任务时，也能够维持运行状态
        try {
            int read = System.in.read();
            System.out.println(LongTermProcess.class.getSimpleName() + ": read=" + read);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static char[] generateContent(int block, int range, int offset) {
        char[] bytes = new char[block];
        int blockIndex = 0;
        while (blockIndex < block) {
            byte alpha = (byte) ((blockIndex % range) + offset);
            bytes[blockIndex] = (char) alpha;
            blockIndex++;
        }
        return bytes;
    }

    private static class PrintTask implements Runnable {

        private final int block;
        private final int count;

        private final PrintStream printStream;
        private final char[] chars;

        public PrintTask(int block, int count, PrintStream printStream, char[] chars) {
            this.block = block;
            this.count = count;
            this.printStream = printStream;
            this.chars = chars;
        }

        @Override
        public void run() {
            long start = System.currentTimeMillis();

            int countIndex = 0;
            while (++countIndex <= count) {
                //打印行号，方便观察当前打印到了第几行，最终计算输出 byte 数时，忽略了行数部分和换行符占用的字节数。
                printStream.printf("%-10d", countIndex);
                printStream.println(chars);
                printStream.flush();
            }

            String threadName = Thread.currentThread().getName();
            int sum = block * count;
            long end = System.currentTimeMillis();
            long duration = end - start;
            String format = String.format("thread named [%s] printed [%s] chars data in [%s] millis.", threadName, sum, duration);
            System.out.println(format);
        }
    }

}
