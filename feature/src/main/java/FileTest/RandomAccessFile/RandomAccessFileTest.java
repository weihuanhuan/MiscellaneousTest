package FileTest.RandomAccessFile;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

/**
 * Created by JasonFitch on 9/11/2019.
 */
public class RandomAccessFileTest {


    public static void main(String[] args) throws IOException {
        String randomAccessFilePath = "RandomAccessFile.log";

        RandomAccessFile randomAccessFile = new RandomAccessFile(randomAccessFilePath, "rw");

        long filePointer = randomAccessFile.getFilePointer();

        //随机定位文件指针所在的位置，可以随意的读取指定内容，可以向前回溯，反复读取
        randomAccessFile.seek(filePointer);

        randomAccessFile.setLength(filePointer);

        int i = randomAccessFile.skipBytes((int) filePointer);

        int ch = randomAccessFile.read();
        byte b = randomAccessFile.readByte();

        //批量读取效率高
        byte[] bytes = new byte[100];
        int read = randomAccessFile.read(bytes);

        //randomAccessFile 默认读取 ISO-8859-1 编码，需要转换，防止乱码
        // readline 会将 filePointer 指向读取了 换行符 后的位置，但是返回的结果里是丢掉换行符的
        String line = randomAccessFile.readLine();
        byte[] lineBytes = line.getBytes("ISO-8859-1");
        String result = new String(lineBytes, "UTF-8");

        //读取完文件
        if (randomAccessFile.getFilePointer() >= randomAccessFile.length()) {
            System.out.println("it's over");
        }

        FileChannel channel = randomAccessFile.getChannel();

    }

}
