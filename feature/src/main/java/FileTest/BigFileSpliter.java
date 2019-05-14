package FileTest;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by JasonFitch on 5/13/2019.
 */
public class BigFileSpliter {

    public static void main(String[] args) throws IOException {

        long start = System.currentTimeMillis();

        String sourcefileStr = "C:/Users/JasonFitch/Desktop/big/c.txt";
        String destfileStr = "C:/Users/JasonFitch/Desktop/big/c-{0}.txt";

        File sourcefile = new File(sourcefileStr);
        if (sourcefile.isFile() && sourcefile.exists()) {

            RandomAccessFile randomAccessFile = new RandomAccessFile(sourcefile, "r");
            FileChannel fileChannel = randomAccessFile.getChannel();

            int blockSize = 0x2000000;
            long lengthOfWhole = randomAccessFile.length();
            long indexCount = (lengthOfWhole / blockSize);
            long remainCount = (lengthOfWhole % blockSize);
            System.out.println("lengthOfWhole=" + lengthOfWhole);
            System.out.println("indexCount=" + indexCount);
            System.out.println("remainCount=" + remainCount);

            ExecutorService executorService = Executors.newCachedThreadPool();
            for (long currentPosition = 0, processBlockSize = blockSize, fileIndex = 1;
                 currentPosition < lengthOfWhole;
                 ++fileIndex, currentPosition += processBlockSize) {

                long remain = lengthOfWhole - currentPosition;
                if (remain < blockSize) {
                    processBlockSize = remain;
                }
                String replaceDestfileStr = destfileStr.replace("{0}", String.valueOf(fileIndex));

                Runnable runnable = new CopyTask(fileChannel, replaceDestfileStr, currentPosition, processBlockSize);
                executorService.submit(runnable);
            }
            executorService.shutdown();

            try {
                // 阻塞，等待所有的拷贝任务完成，否则shutdown不会阻塞等待，导致在为完成拷贝任务时直接调用下面的close出现 ClosedChannelException.
                executorService.awaitTermination(1000 * 60, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            fileChannel.close();
            randomAccessFile.close();
        } else {
            System.out.println("no exist");
        }

        System.out.println(BigFileSpliter.class.getSimpleName() + " spend: " + (double) (System.currentTimeMillis() - start) / 1000 + "s");
    }

}

class CopyTask implements Runnable {

    private FileChannel fileChannel;
    private String replaceDestfileStr;
    private long currentPosition;
    private long processBlockSize;

    public CopyTask(FileChannel fileChannel, String replaceDestfileStr, long currentPosition, long processBlockSize) {
        this.fileChannel = fileChannel;
        this.replaceDestfileStr = replaceDestfileStr;
        this.currentPosition = currentPosition;
        this.processBlockSize = processBlockSize;
    }

    @Override
    public void run() {
        try {
            MappedByteBuffer inMap = fileChannel.map(FileChannel.MapMode.READ_ONLY, currentPosition, processBlockSize);
            long sum = currentPosition + processBlockSize;
            System.out.println(currentPosition + " + " + processBlockSize + " = " + sum);

            File destfile = new File(replaceDestfileStr);
            RandomAccessFile destRaf = new RandomAccessFile(destfile, "rw");
            FileChannel channel = destRaf.getChannel();

            MappedByteBuffer outMap = channel.map(FileChannel.MapMode.READ_WRITE, 0, processBlockSize);

            long start = System.currentTimeMillis();
            for (int i = 0; i < processBlockSize; i++) {
                byte b = inMap.get(i);
                outMap.put(i, b);
            }
            System.out.println(replaceDestfileStr + " " + "spend: " + (double) (System.currentTimeMillis() - start) / 1000 + "s");

            channel.close();
            destRaf.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
