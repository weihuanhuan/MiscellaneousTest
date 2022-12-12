package xml.helper;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import xml.document.XmlDocumentParser;
import xml.xpath.XPathExpressionConvertor;
import xml.xpath.XPathExpressionEvaluator;

import java.io.File;
import java.util.Objects;

public class XmlHelper {

    public static Document getXmlDocument(String xmlString) {
        return XmlDocumentParser.parseDocument(xmlString);
    }

    public static Document getXmlDocument(File file) {
        return XmlDocumentParser.parseDocument(file);
    }

    public static Node getNode(File file, String expression) {
        Document xmlDocument = getXmlDocument(file);
        return XPathExpressionEvaluator.evaluateToNode(xmlDocument, expression);
    }

    public static int countNode(File file, String expression) {
        NodeList nodeList = XmlHelper.getNodeSet(file, expression);
        if (nodeList == null) {
            return 0;
        }
        return nodeList.getLength();
    }

    public static NodeList getNodeSet(File file, String expression) {
        Document xmlDocument = getXmlDocument(file);
        return XPathExpressionEvaluator.evaluateToNodeSet(xmlDocument, expression);
    }

    public static String getNodeXmlString(File file, String expression) {
        Document xmlDocument = getXmlDocument(file);
        return XPathExpressionConvertor.convertNodeToXmlString(xmlDocument, expression);
    }

    public static String getNodeSetXmlString(File file, String expression) {
        Document xmlDocument = getXmlDocument(file);
        return XPathExpressionConvertor.convertNodeSetToXmlString(xmlDocument, expression);
    }

    public static Node getXmlStringRootNode(String xmlString) {
        Document xmlDocument = getXmlDocument(xmlString);

        // xml string 转换的 expectXmlDocument 的 org.w3c.dom.Node.getOwnerDocument 为 null
        Document ownerDocument = xmlDocument.getOwnerDocument();
        // xml string 转换的 expectXmlDocument 的 org.w3c.dom.Document.getDocumentElement 即为 root node, 其实现了 org.w3c.dom.Node,
        Element documentElement = xmlDocument.getDocumentElement();
        return documentElement;
    }

    public static boolean compareNodesDirect(Node firstNode, Node secondNode) {
        // null reference compare
        if (firstNode == null) {
            return secondNode == null;
        }
        if (secondNode == null) {
            return false;
        }

        // direct node compare
        // compare with jdk java.lang.Object.equals
        boolean equals = firstNode.equals(secondNode);
        // compare with xml node object impl
        boolean sameNode = firstNode.isSameNode(secondNode);
        // compare with xml node object attribute and element value
        boolean equalNode = firstNode.isEqualNode(secondNode);
        return equalNode;
    }

    public static boolean compareNodesNormalized(Node firstNode, Node secondNode) {
        // null reference compare
        if (firstNode == null) {
            return secondNode == null;
        }
        if (secondNode == null) {
            return false;
        }

        // The documents need to be cloned as normalization has side-effects
        firstNode = firstNode.cloneNode(true);
        secondNode = secondNode.cloneNode(true);

        // The documents need to be normalized before comparison takes place to remove any formatting that interfere with comparison
        if (firstNode instanceof Document && secondNode instanceof Document) {
            ((Document) firstNode).normalizeDocument();
            ((Document) secondNode).normalizeDocument();
        } else {
            firstNode.normalize();
            secondNode.normalize();
        }

        String firstNodeXmlString = XmlDocumentParser.transformerToXmlString(firstNode);
        String secondNodeXmlString = XmlDocumentParser.transformerToXmlString(secondNode);
        boolean equals = Objects.equals(firstNodeXmlString, secondNodeXmlString);
        return equals;
    }

    public static String getTextString(File file, String expression) {
        Document xmlDocument = getXmlDocument(file);
        return XPathExpressionEvaluator.evaluateToTextString(xmlDocument, expression);
    }

}