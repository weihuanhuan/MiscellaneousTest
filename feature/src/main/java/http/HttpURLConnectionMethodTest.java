package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by JasonFitch on 5/21/2021.
 */
public class HttpURLConnectionMethodTest {

    public static void main(String[] args) {
        String urlStr = "http://192.168.88.202:8080/console/system/login.action";

        String encodedURL = java.net.URLEncoder.encode(urlStr);
        System.out.println(String.format("java.net.URLEncoder.encode(urlStr) = %s", encodedURL));

        try {
            URL url = new URL(urlStr);
            //配置并连接
            HttpURLConnection httpConn = openConnection(url);

            //GET - 用户代码配置的请求方法
            String requestMethod = httpConn.getRequestMethod();
            System.out.println(String.format("httpConn.getRequestMethod() is %s after httpConn.connect()", requestMethod));

            //发送数据
            sendData(httpConn);

            //POST - jdk 实际使用的请求方法
            requestMethod = httpConn.getRequestMethod();
            System.out.println(String.format("httpConn.getRequestMethod() is %s after httpConn.getOutputStream()", requestMethod));

            //检查响应
            checkResponse(httpConn);

            //POST - jdk 实际使用的请求方法
            requestMethod = httpConn.getRequestMethod();
            System.out.println(String.format("httpConn.getRequestMethod() is %s after httpConn.getResponseCode()", requestMethod));

            //读取响应
            readResponse(httpConn);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static HttpURLConnection openConnection(URL url) throws IOException {
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();

        //设置请求 method
        httpConn.setRequestMethod("GET");

        //设置请求 header
        httpConn.setRequestProperty("Host", "localhost:8080");
        httpConn.setRequestProperty("Connection", "Close");

        //控制是否可以向 http 请求体 中写入数据。
        httpConn.setDoOutput(true);

        //防止没有数据时阻塞
        httpConn.setConnectTimeout(5 * 1000);
        httpConn.setReadTimeout(5 * 1000);

        //连接，必须在各种 set 请求状态 之后调用，改方法只建立网络连接，并不会发送 http 报文数据。
        httpConn.connect();
        return httpConn;
    }

    private static void sendData(HttpURLConnection httpConn) throws IOException {
        //对于 jdk 的实现，当设置了 httpConn.setDoOutput(true) 后，再调用 httpConn.getOutputStream() 会隐式的将 get 方法变为 post
        //但是这是因为 get 请求不能在请求体中写数据，而 post 请求是可以的。

        //另外当设置了 httpConn.setDoOutput(true) 后，不调用 httpConn.getOutputStream() 就不会隐式的将 get 方法变为 post。
        //这里要注意这个细节，因为 digest 的认证摘要是包含 http request method 的，所以 method 的变更会影响 digest 的认证处理。
        OutputStream outputStream = httpConn.getOutputStream();
        outputStream.flush();

        outputStream.close();
    }

    private static void checkResponse(HttpURLConnection httpConn) throws IOException {
        //这里的 java.net.HttpURLConnection.getResponseCode 会触发 jdk 实现真正的向网络流中写入数据，此时才真正的执行 http 请求。
        int responseCode = httpConn.getResponseCode();
        System.out.println(String.format("httpConn.getResponseCode() = %s", responseCode));

        String responseMessage = httpConn.getResponseMessage();
        System.out.println(String.format("httpConn.getResponseMessage() = %s", responseMessage));
    }

    private static void readResponse(HttpURLConnection httpConn) throws IOException {
        InputStream is = httpConn.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        String line;
        while (null != (line = br.readLine())) {
            System.out.println(line);
        }

        br.close();
    }
}
