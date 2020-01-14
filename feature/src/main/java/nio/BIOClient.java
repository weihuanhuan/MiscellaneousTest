package nio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Scanner;

public class BIOClient {

    ByteBuffer writeBuffer = ByteBuffer.allocate(1024);
    ByteBuffer readBuffer = ByteBuffer.allocate(1024);

    public void start(int port) throws IOException {

        Socket socket = new Socket();
        socket.connect(new InetSocketAddress("localhost", port));

        boolean isWrite = true;
        Scanner scanner = new Scanner(System.in);
        int count = 0;
        while (true) {

            if (isWrite) {
                System.out.print("please input message:");
                String message = scanner.nextLine();
                //ByteBuffer writeBuffer = ByteBuffer.wrap(message.getBytes());
                writeBuffer.clear();
                writeBuffer.put(message.getBytes());
                //将缓冲区各标志复位,因为向里面put了数据标志被改变要想从中读取数据发向服务器,就要复位
                writeBuffer.flip();

                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(writeBuffer.array(), 0, message.length());

                isWrite = !isWrite;
            } else {
                //读取数据
                System.out.print("receive message:");
                //将缓冲区清空以备下次读取
                readBuffer.clear();

                InputStream inputStream = socket.getInputStream();
                //从客户端socket的使用角度看，什么情况下返回 -1 ？
                int read = inputStream.read(readBuffer.array());
                System.out.println(new String(readBuffer.array(), 0, read));

                isWrite = !isWrite;

                ++count;
                if (count > 1) {
                    throw new OutOfMemoryError();
                }
            }

        }
    }

    public static void main(String[] args) throws IOException {
        new BIOClient().start(28000);
    }

}
