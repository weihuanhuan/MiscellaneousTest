package network.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Created by JasonFitch on 9/19/2019.
 */
public class HttpURLConnectionProxyTest {

    public static void main(String[] args) {
        String urlStr = "https://www.google.com";

        String encodedURL = java.net.URLEncoder.encode(urlStr);
        System.out.println(String.format("encoded request url: [%s]", encodedURL));

        try {
            //设置代理服务器
            Proxy webProxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 1081));

            URL url = new URL(urlStr);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection(webProxy);

            //设置请求 method
            httpConn.setRequestMethod("GET");

            //设置请求 header
            httpConn.setRequestProperty("Host", "www.google.com");
            httpConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0)");
            httpConn.setRequestProperty("Accept", "*/*");
            httpConn.setRequestProperty("Connection", "Close");

            httpConn.setInstanceFollowRedirects(true);
            httpConn.setUseCaches(false);

            //防止没有数据时阻塞
            httpConn.setConnectTimeout(5 * 1000);
            httpConn.setReadTimeout(5 * 1000);

            //连接，必须在各种 set请求状态 之后调用
            httpConn.connect();

            System.out.println("############# response line ##############");

            int responseCode = httpConn.getResponseCode();
            String responseMessage = httpConn.getResponseMessage();
            System.out.println(String.format("responseCode ---> responseMessage: [%s] ---> [%s]", responseCode, responseMessage));

            System.out.println("############# response header ##############");

            Map<String, List<String>> headerFields = httpConn.getHeaderFields();
            for (Map.Entry<String, List<String>> entry : headerFields.entrySet()) {
                String key = entry.getKey();
                //可能存在一个头 key 对应多个 头 value 的情况，所以 value 使用 List<String> 来表示。
                List<String> value = entry.getValue();
                for (String s : value) {
                    System.out.println(String.format("header=value ---> [%s]=[%s]", key, s));
                }
            }

            System.out.println("############# response body ##############");

            InputStream is = httpConn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while (null != (line = br.readLine())) {
                System.out.println(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
