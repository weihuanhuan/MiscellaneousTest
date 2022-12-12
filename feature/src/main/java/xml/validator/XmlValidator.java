package xml.validator;

import org.w3c.dom.Node;
import xml.helper.XmlHelper;

import java.io.File;
import java.util.Objects;

public class XmlValidator {

    public static boolean existNode(File file, String expression) {
        Node node = XmlHelper.getNode(file, expression);
        return node != null;
    }

    public static boolean equalNodeCount(File file, String expression, int expectCount) {
        int count = XmlHelper.countNode(file, expression);
        return count == expectCount;
    }

    public static boolean equalXmlStringByString(File file, String expression, String expectXmlString) {
        String xmlString = XmlHelper.getNodeXmlString(file, expression);
        return Objects.equals(xmlString, expectXmlString);
    }

    public static boolean equalXmlStringByNode(File file, String expression, String expectNodeXmlString) {
        Node evaluateToNode = XmlHelper.getNode(file, expression);

        Node expectNode = XmlHelper.getXmlStringRootNode(expectNodeXmlString);

        // simple node compare
        boolean compareNodesDirect = XmlHelper.compareNodesDirect(evaluateToNode, expectNode);

        // complex node compare
        boolean compareNodesNormalized = XmlHelper.compareNodesNormalized(evaluateToNode, expectNode);

        boolean equals = Objects.equals(compareNodesDirect, compareNodesNormalized);
        System.out.println("Objects.equals(compareNodesDirect, compareNodesNormalized)=" + equals);
        return compareNodesNormalized;
    }

    public static boolean equalTextString(File file, String expression, String expectTextString) {
        String textString = XmlHelper.getTextString(file, expression);
        return Objects.equals(textString, expectTextString);
    }

}