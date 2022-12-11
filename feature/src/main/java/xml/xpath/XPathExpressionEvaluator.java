package xml.xpath;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public class XPathExpressionEvaluator {

    public static XPathExpression newXPathExpression(String expression) {
        XPath xPath = XPathFactory.newInstance().newXPath();
        try {
            XPathExpression compile = xPath.compile(expression);
            return compile;
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }

    public static String evaluateToTextString(Document xmlDocument, String expression) {
        XPathExpression xPathExpression = newXPathExpression(expression);
        String evaluate = evaluateToTextString(xmlDocument, xPathExpression);
        return evaluate;
    }

    public static String evaluateToTextString(Document xmlDocument, XPathExpression compile) {
        String evaluate = (String) evaluate(xmlDocument, compile, XPathConstants.STRING);
        return evaluate;
    }

    public static Node evaluateToNode(Document xmlDocument, String expression) {
        XPathExpression xPathExpression = newXPathExpression(expression);
        Node evaluate = evaluateToNode(xmlDocument, xPathExpression);
        return evaluate;
    }

    public static Node evaluateToNode(Document xmlDocument, XPathExpression compile) {
        Node evaluate = (Node) evaluate(xmlDocument, compile, XPathConstants.NODE);
        return evaluate;
    }

    public static NodeList evaluateToNodeSet(Document xmlDocument, String expression) {
        XPathExpression xPathExpression = newXPathExpression(expression);
        NodeList evaluate = evaluateToNodeSet(xmlDocument, xPathExpression);
        return evaluate;
    }

    public static NodeList evaluateToNodeSet(Document xmlDocument, XPathExpression compile) {
        NodeList evaluate = (NodeList) evaluate(xmlDocument, compile, XPathConstants.NODESET);
        return evaluate;
    }

    public static Object evaluate(Document xmlDocument, String expression, QName qName) {
        XPathExpression xPathExpression = newXPathExpression(expression);
        Object evaluate = evaluate(xmlDocument, xPathExpression, qName);
        return evaluate;
    }

    public static Object evaluate(Document xmlDocument, XPathExpression compile, QName qName) {
        try {
            Object evaluate = compile.evaluate(xmlDocument, qName);
            return evaluate;
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }

}