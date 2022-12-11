package xml;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import xml.document.XmlDocumentParser;
import xml.xpath.XPathExpressionBuilder;
import xml.xpath.XPathExpressionConvertor;
import xml.xpath.XPathExpressionEvaluator;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

public class XPathExpressionTest {

    public static void main(String[] args) throws URISyntaxException {

        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        URL resource = contextClassLoader.getResource("xml/xpath/xpath.xml");

        File file = new File(resource.toURI());

        Document document = XmlDocumentParser.parseDocument(file);

        System.out.println("################## attributeOperationValueTest ##################");
        attributeOperationValueTest(document);

        System.out.println("################## functionForCurrentExpressionTest ##################");
        functionForCurrentExpressionTest(document);

        System.out.println("################## elementNameOperationValueEqualTest ##################");
        elementNameOperationValueEqualTest(document);

        System.out.println("################## elementNameOperationValueTest ##################");
        elementNameOperationValueTest(document);
    }

    private static void attributeOperationValueTest(Document document) {
        String id = "01";
        String directExpression = "/Tutorials/Tutorial[@tutId=" + "'" + id + "'" + "]";

        String buildExpression = new XPathExpressionBuilder()
                .withElementName("Tutorials")
                .withElementName("Tutorial")
                .withAttributeNameOperationValue("tutId", "=", id)
                .build();

        printEvaluatedXPathExpression(document, directExpression, buildExpression);
    }

    private static void functionForCurrentExpressionTest(Document document) {
        String id = "01";
        String directExpression = "name(/Tutorials/Tutorial[@tutId=" + "'" + id + "'" + "])";

        String buildExpression = new XPathExpressionBuilder()
                .withElementName("Tutorials")
                .withElementName("Tutorial")
                .withAttributeNameOperationValue("tutId", "=", id)
                .withFunctionForCurrentExpression("name")
                .build();

        printEvaluatedXPathExpression(document, directExpression, buildExpression);
    }

    private static void elementNameOperationValueEqualTest(Document document) {
        String directExpression = "/Tutorials/Tutorial[title='XML']";

        String buildExpression = new XPathExpressionBuilder()
                .withElementName("Tutorials")
                .withElementName("Tutorial")
                .withElementNameOperationValue("title", "=", "XML")
                .build();

        printEvaluatedXPathExpression(document, directExpression, buildExpression);
    }

    private static void elementNameOperationValueTest(Document document) {
        String directExpression = "/Tutorials/Tutorial[price>'5']";

        String buildExpression = new XPathExpressionBuilder()
                .withElementName("Tutorials")
                .withElementName("Tutorial")
                .withElementNameOperationValue("price", ">", "5")
                .build();

        printEvaluatedXPathExpression(document, directExpression, buildExpression);
    }

    private static void printEvaluatedXPathExpression(Document xmlDocument, String directExpression, String buildExpression) {
        System.out.println("---- basic ----");
        System.out.println("directExpression=" + directExpression);
        System.out.println("buildExpression=" + buildExpression);

        boolean equals = Objects.equals(directExpression, buildExpression);
        System.out.println("Objects.equals(directExpression, buildExpression)=" + equals);

        System.out.println("---- evaluateToTextString ----");
        String textStringByBuildExpression = XPathExpressionEvaluator.evaluateToTextString(xmlDocument, buildExpression);
        System.out.println(textStringByBuildExpression);

        System.out.println("---- convertNodeToXmlString ----");
        try {
            String xmlStringByBuildExpression = XPathExpressionConvertor.convertNodeToXmlString(xmlDocument, buildExpression);
            System.out.println(xmlStringByBuildExpression);
        } catch (RuntimeException exception) {
            System.out.println(exception.getMessage());
        }

        System.out.println("---- evaluateToNodeSet ----");
        try {
            NodeList nodeList = XPathExpressionEvaluator.evaluateToNodeSet(xmlDocument, buildExpression);
            int length = nodeList.getLength();
            System.out.println("nodeList.getLength()=" + length);

            String nodeListStringByBuildExpression = XPathExpressionConvertor.convertNodeSetToXmlString(xmlDocument, buildExpression);
            System.out.println(nodeListStringByBuildExpression);
        } catch (RuntimeException exception) {
            System.out.println(exception.getMessage());
        }
    }

}
