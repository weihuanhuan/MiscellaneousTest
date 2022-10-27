package network.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by JasonFitch on 9/19/2019.
 */
public class HttpTest {

    public static void main(String[] args) {
        String urlStr = "http://192.168.1.28:8080/console/system/login.action";

        String encodedURL = java.net.URLEncoder.encode(urlStr);
        System.out.println(encodedURL);

        //POST 请求时可以通过 application/x-www-form-urlencoded 将表单数据放入请求体中
        String body = "j_username=admin&j_password=OsKrwNSSNXwTS1zofv%2BLx4vI9shyBsVlPBG%2BtjgAMk1AaCm9IGYe0%2FFzpQJ23NDBskC%2BFVsZnnS8aEe6a1OQppxcNOXRL88cwjpYTb6Pz75TU7RdPb9Cu6sXP536hEOJXkin14rnzNpigMW8o48Qi2n1a0nWKmD69MR8TzczqJQ%3D";

        try {
            URL url = new URL(urlStr);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();

            //设置请求 method
            httpConn.setRequestMethod("POST");

            //设置请求 header
            httpConn.setRequestProperty("Host", "WeiHuanHuan:8080");
            httpConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0)");
            httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpConn.setRequestProperty("Content-Length", String.valueOf(body.getBytes("UTF-8").length));
            httpConn.setRequestProperty("Accept", "*/*");
            httpConn.setRequestProperty("Connection", "Close");

            httpConn.setInstanceFollowRedirects(true);
            httpConn.setUseCaches(false);

            //输出POST请求的表单数据，需要使用输出流，其默认是false
            //读取服务器返回的数据，对于输入流，则默认是 true
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);

            //防止没有数据时阻塞
            httpConn.setConnectTimeout(5 * 1000);
            httpConn.setReadTimeout(5 * 1000);

            //连接，必须在各种 set请求状态 之后调用
            httpConn.connect();

            OutputStream outputStream = httpConn.getOutputStream();
            outputStream.write(body.getBytes("UTF-8"));
            //否则数据无法写出
            outputStream.flush();
            outputStream.close();

            int responeCode = httpConn.getResponseCode();
            System.out.println(responeCode);

            String responseMessage = httpConn.getResponseMessage();
            System.out.println(responseMessage);

            InputStream is = httpConn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while (null != (line = br.readLine())) {
                System.out.println(line);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
