package xml;

import xml.helper.XmlHelper;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

public class XmlHelperTest {

    public static void main(String[] args) throws URISyntaxException {

        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        URL resource = contextClassLoader.getResource("xml/xpath/xpath.xml");

        File file = new File(resource.toURI());

        System.out.println("################## countNodeElementEqualTest ##################");
        countNodeElementEqualTest(file);

        System.out.println("################## countNodeElementGreatTest ##################");
        countNodeElementGreatTest(file);

        System.out.println("################## getTextStringTest ##################");
        getTextStringTest(file);

    }

    private static void countNodeElementEqualTest(File file) {
        String directExpression = "/Tutorials/Tutorial[title='XML']";

        int countNode = XmlHelper.countNode(file, directExpression);
        System.out.println(countNode);
    }

    private static void countNodeElementGreatTest(File file) {
        String directExpression = "/Tutorials/Tutorial[price>'5']";

        int countNode = XmlHelper.countNode(file, directExpression);
        System.out.println(countNode);
    }

    private static void getTextStringTest(File file) {
        String directExpression2 = "/Tutorials/Tutorial[@tutId='01']";
        System.out.println("-------" + directExpression2);
        String nodeValueString2 = XmlHelper.getTextString(file, directExpression2);
        System.out.println(nodeValueString2);

        String directExpression3 = "/Tutorials/Tutorial";
        System.out.println("-------" + directExpression3);
        String nodeValueString3 = XmlHelper.getTextString(file, directExpression3);
        System.out.println(nodeValueString3);

        String directExpression4 = "/Tutorials";
        System.out.println("-------" + directExpression4);
        String nodeValueString4 = XmlHelper.getTextString(file, directExpression4);
        System.out.println(nodeValueString4);

        String directExpression5 = "/Tutorials/Tutorial[@tutId='01']/@type";
        System.out.println("-------" + directExpression5);
        String nodeValueString5 = XmlHelper.getTextString(file, directExpression5);
        System.out.println(nodeValueString5);
    }

}
