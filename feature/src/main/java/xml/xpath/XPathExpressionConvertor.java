package xml.xpath;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import xml.document.XmlDocumentParser;

import javax.xml.xpath.XPathExpression;

public class XPathExpressionConvertor {

    public static String convertNodeToXmlString(Document xmlDocument, String expression) {
        XPathExpression xPathExpression = XPathExpressionEvaluator.newXPathExpression(expression);
        String xmlString = convertNodeToXmlString(xmlDocument, xPathExpression);
        return xmlString;
    }

    public static String convertNodeToXmlString(Document xmlDocument, XPathExpression compile) {
        Node evaluate = XPathExpressionEvaluator.evaluateToNode(xmlDocument, compile);
        if (evaluate == null) {
            return null;
        }

        String xmlString = XmlDocumentParser.transformerToXmlString(evaluate);
        return xmlString;
    }

    public static String convertNodeSetToXmlString(Document xmlDocument, String expression) {
        XPathExpression xPathExpression = XPathExpressionEvaluator.newXPathExpression(expression);
        return convertNodeSetToXmlString(xmlDocument, xPathExpression);
    }

    public static String convertNodeSetToXmlString(Document xmlDocument, XPathExpression compile) {
        NodeList evaluate = XPathExpressionEvaluator.evaluateToNodeSet(xmlDocument, compile);
        if (evaluate == null) {
            return null;
        }

        return XmlDocumentParser.transformerToXmlString(evaluate);
    }

}