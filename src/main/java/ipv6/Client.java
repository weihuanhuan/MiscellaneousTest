package ipv6;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by JasonFitch on 8/15/2018.
 */
public class Client
{
    public static void main(String[] args)
    {

        String encodedURL = java.net.URLEncoder.encode("http://[fe80::20c:29ff:fe68:65ec%7]:8080/BEStest-1.0-SNAPSHOT/request");
        System.out.println(encodedURL);
//        http%3A%2F%2F%5Bfe80%3A%3A20c%3A29ff%3Afe68%3A65ec%257%5D%3A8080%2FBEStest-1.0-SNAPSHOT%2Frequest


        try
        {
//            Socket sc = new Socket("[fe80::20c:29ff:fe68:65ec]", 65010);



            Socket sc = new Socket("[fe80::18c9:8e29:8d2c:acd3%7]", 65010);
//            Socket sc = new Socket("[fe80::18c9:8e29:8d2c:acd3]", 65010);
//           对于本地监听的LocalLink地址，客户端指不指定接口序号都可以建立连接。

            InputStream is = sc.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while (null != (line = br.readLine()))
            {
                System.out.println(line);
            }


        } catch (IOException e)
        {
            e.printStackTrace();
        }


    }

}
