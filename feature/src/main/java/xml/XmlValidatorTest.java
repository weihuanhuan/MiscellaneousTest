package xml;

import xml.validator.XmlValidator;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

public class XmlValidatorTest {

    public static void main(String[] args) throws URISyntaxException {

        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        URL resource = contextClassLoader.getResource("xml/xpath/xpath.xml");

        File file = new File(resource.toURI());

        System.out.println("################## existNodeTest ##################");
        existNodeTest(file);

        System.out.println("################## equalXmlStringByStringTest ##################");
        equalXmlStringByStringTest(file);

        System.out.println("################## equalXmlStringByNodeTest ##################");
        equalXmlStringByNodeTest(file);

        System.out.println("################## equalXmlStringByNodeNestedTest ##################");
        equalXmlStringByNodeNestedTest(file);

        System.out.println("################## equalTextStringTest ##################");
        equalTextStringTest(file);

    }

    private static void existNodeTest(File file) {
        String directExpression = "/Tutorials/Tutorial[@tutId='01']";
        boolean existNode = XmlValidator.existNode(file, directExpression);
        System.out.println(existNode);
    }

    private static void equalXmlStringByStringTest(File file) {
        //直接比较 xml string 时，可能由于 expect xml string 的格式或者换行问题导致错误的将相同的 node string 判断为不相同的
        String directExpression1 = "/Tutorials/Tutorial[@tutId='01']/title";
        boolean equalNodeXmlString1 = XmlValidator.equalXmlStringByString(file, directExpression1, "\n<title>Guava</title>\n");
        System.out.println(equalNodeXmlString1);
    }

    private static void equalXmlStringByNodeTest(File file) {
        //这里将 xml string 解析为 org.w3c.dom.Node 后在进行比较，就不会受到格式或者换行问题的问题干扰了。
        String directExpression1 = "/Tutorials/Tutorial[@tutId='01']/title";
        boolean equalNodeXmlString1 = XmlValidator.equalXmlStringByNode(file, directExpression1, "\n<title>Guava</title>\n");
        System.out.println(equalNodeXmlString1);

        //xml string 的属性顺序不影响 node 的比较，但是 element 的顺序却会使得基于 node 的比较不相等。
        //这里交换了，针对 attribute 的 type 与 tutId 的顺序，以及针对 element 的 <description> 与 <date> 的顺序
        String directExpression2 = "/Tutorials/Tutorial[@tutId='01']";
        boolean equalNodeXmlString2 = XmlValidator.equalXmlStringByNode(file, directExpression2,
                "<Tutorial type=\"java\" tutId=\"01\" >\n" +
                "        <title>Guava</title>\n" +
                "        <date>04/04/2016</date>\n" +
                "        <description>Introduction to Guava</description>\n" +
                "        <author>GuavaAuthor</author>\n" +
                "        <price>3.50</price>\n" +
                "    </Tutorial>");
        System.out.println(equalNodeXmlString2);
    }

    private static void equalXmlStringByNodeNestedTest(File file) {
        String directExpression = "/Tutorials/Tutorial[@tutId='04']/title";

        //这里的 expectNodeXmlString 为直接从原始的 xml 文本中复制过来的，所以他们的格式，内容，和 xml 节点顺序都是相同的
        boolean equalNodeXmlString = XmlValidator.equalXmlStringByNode(file, directExpression,
                "<title>\n" +
                        "            <nested nested-attribute=\"nested-value\">nested-text</nested>\n" +
                        "        </title>");
        System.out.println(equalNodeXmlString);

        //注意在 xml 中，一个元素的子元素如果换行了，其父元素和子元素中间会存在一个 text 类型的 node 元素，
        // 这会导致密集排列的 xml string 和缩进排列的 xml node 不同。
        // 其 node 实现 为 com.sun.org.apache.xerces.internal.dom.DeferredTextImpl
        // 其 node 值为 【 \n + 从父元素的结束标签到子元素开始标签的之间的多个空格】组成的字符串。
        boolean equalNodeXmlString2 = XmlValidator.equalXmlStringByNode(file, directExpression,
                "<title>" +
                        "<nested nested-attribute=\"nested-value\">nested-text</nested>" +
                        "</title>");
        System.out.println(equalNodeXmlString2);
    }

    private static void equalTextStringTest(File file) {
        //element element text
        String directExpression1 = "/Tutorials/Tutorial[@tutId='01']/title";
        boolean equalTextString1 = XmlValidator.equalTextString(file, directExpression1, "Guava");
        System.out.println(equalTextString1);

        //element attribute text
        String directExpression2 = "/Tutorials/Tutorial[@tutId='01']/@type";
        boolean equalTextString2 = XmlValidator.equalTextString(file, directExpression2, "java");
        System.out.println(equalTextString2);
    }

}
