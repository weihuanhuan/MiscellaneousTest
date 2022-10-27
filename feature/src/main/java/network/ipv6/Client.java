package network.ipv6;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by JasonFitch on 8/15/2018.
 */
public class Client
{
    public static void main(String[] args)
    {
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
                System.out.println(line);


        } catch (IOException e)
        {
            e.printStackTrace();
        }

    }

}
