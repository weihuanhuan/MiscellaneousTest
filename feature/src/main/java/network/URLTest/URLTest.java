package network.URLTest;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JasonFitch on 5/16/2020.
 */
public class URLTest {

    public static void main(String[] args) {

        legalURLTest();

    }

    private static void legalURLTest() {

        //http
        String url1 = "http://dummy/a";
        String url2 = "https://dummy/a";

        //file
        String url3 = "file://dummy/a";

        //jar
        String url4 = "jar://dummy/a";

        //JNDI
        String url51 = "java:/dummy/a";
        String url52 = "java:comp/dummy/a";
        String url6 = "/dummy/a";
        String url7 = "dummy/a";
        String url8 = "dummy";

        //other
        String url9 = "tcp://dummy/a";
        String url10 = "java://dummy/a";
        String url11 = "url://dummy/a";


        List<String> stringList = new ArrayList<>();

        stringList.add(url1);
        stringList.add(url2);
        stringList.add(url3);
        stringList.add(url4);
        stringList.add(url51);
        stringList.add(url52);
        stringList.add(url6);
        stringList.add(url7);
        stringList.add(url8);
        stringList.add(url9);
        stringList.add(url10);
        stringList.add(url11);

        createURL(stringList);
    }

    private static void createURL(List<String> stringList) {
        if (stringList == null || stringList.isEmpty()) {
            return;

        }

        for (String url : stringList) {
            try {
                new URL(url);
            } catch (MalformedURLException e) {
                System.out.println(url + " is illegal url!");
            }
        }

    }

}
