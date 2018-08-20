package ipv6;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by JasonFitch on 8/17/2018.
 */
public class JavaClientHttp
{
    public static void main(String[] args)
    {
        String urlStr = "http://[fe80:0:0:0:20c:29ff:fe68:65ec]:8080/BEStest-1.0-SNAPSHOT/request";
        String encodedURL = java.net.URLEncoder.encode(urlStr);
        System.out.println(encodedURL);
//       http%3A%2F%2F%5Bfe80%3A0%3A0%3A0%3A20c%3A29ff%3Afe68%3A65ec%5D%3A8080%2FBEStest-1.0-SNAPSHOT%2Frequest

        try
        {
            URL url = new URL(urlStr);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();

            httpConn.setRequestMethod("GET");
//            httpConn.setRequestProperty("Host","[fe80::18c9:8e29:8d2c:acd3]");
//            httpConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.62 Safari/537.36");

            int responeCode = httpConn.getResponseCode();
            System.out.println(responeCode);

            if (HttpURLConnection.HTTP_OK == responeCode)
            {
                InputStream is = httpConn.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line;
                while (null != (line = br.readLine()))
                    System.out.println(line);
            }

        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }


    }
}
