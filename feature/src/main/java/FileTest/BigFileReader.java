package FileTest;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.Executors;

/**
 * Created by JasonFitch on 5/13/2019.
 */
public class BigFileReader {

    public static void main(String[] args) throws IOException {

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

            for (long currentPosition = 0, processBlockSize = blockSize, fileIndex = 1;
                 currentPosition < lengthOfWhole;
                 ++fileIndex, currentPosition += processBlockSize) {

                long remain = lengthOfWhole - currentPosition;
                if (remain < blockSize) {
                    processBlockSize = remain;
                }
                String replaceDestfileStr = destfileStr.replace("{0}", String.valueOf(fileIndex));

                Runnable runnable = new CopyTask(fileChannel, replaceDestfileStr, currentPosition, processBlockSize);
                Executors.newCachedThreadPool().submit(runnable);

            }
        } else {
            System.out.println("no exist");
        }

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
            MappedByteBuffer map = fileChannel.map(FileChannel.MapMode.READ_ONLY, currentPosition, processBlockSize);
            long sum = currentPosition + processBlockSize;
            System.out.println(currentPosition + " + " + processBlockSize + " = " + sum);

            CharBuffer charBuffer = map.asCharBuffer();
            CharBuffer buffer = CharBuffer.wrap(new char[(int) processBlockSize]);
            charBuffer.read(buffer);
            char[] array = buffer.array();

            File destfile = new File(replaceDestfileStr);
            RandomAccessFile destRaf = new RandomAccessFile(destfile, "rw");
            destRaf.writeChars(new String((array)));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
