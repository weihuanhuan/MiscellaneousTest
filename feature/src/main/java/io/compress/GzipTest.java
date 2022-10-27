package io.compress;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * https://bugs.openjdk.java.net/browse/JDK-8081450
 */
public class GzipTest {

    public static void main(String[] args) {
        try {
            byte[] part1 = zip(stream("hello ".getBytes()));
            byte[] part2 = zip(stream("world".getBytes()));

            //联合流测试 hello world
            InputStream zippedData = new SequenceInputStream(stream(part1), stream(part2));
            byte[] data = unzip(zippedData);
            System.out.println(new String(data));

            //独立使用流测试 hello
            InputStream zippedData1 = stream(part1);
            byte[] data1 = unzip(zippedData1);
            System.out.println(new String(data1));

            //独立使用流测试 world
            InputStream zippedData2 = stream(part2);
            byte[] data2 = unzip(zippedData2);
            System.out.println(new String(data2));
        } catch (IOException x) {
            x.printStackTrace();
        }
    }

    private static InputStream stream(byte[] data) {
        return new ByteArrayInputStream(data);
    }

    private static byte[] zip(InputStream data) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream zout = new GZIPOutputStream(out);
        copy(data, zout);
        data.close();
        zout.close();
        return out.toByteArray();
    }

    private static byte[] unzip(InputStream zippedData) throws IOException {
        GZIPInputStream zin = new GZIPInputStream(zippedData);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        copy(zin, out);
        zin.close();
        out.close();
        return out.toByteArray();
    }

    private static void copy(InputStream source, OutputStream sink) throws IOException {
        byte[] buf = new byte[4096];
        int n;
        while ((n = source.read(buf)) > 0) {
            sink.write(buf, 0, n);
        }
    }

}
