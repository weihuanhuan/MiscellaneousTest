package xml.document;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

public class XmlDocumentParser {

    public static Document parseDocument(String xmlString) {
        StringReader stringReader = new StringReader(xmlString);
        InputSource inputSource = new InputSource(stringReader);
        return parseDocument(inputSource);
    }

    public static Document parseDocument(File file) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            InputSource inputSource = new InputSource(fileInputStream);
            return parseDocument(inputSource);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Document parseDocument(InputSource inputSource) {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);

        try {
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document xmlDocument = builder.parse(inputSource);
            return xmlDocument;
        } catch (SAXException | IOException | ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public static String transformerToXmlString(Node sourceNode) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            //保持 org.w3c.dom.NodeList 转换为 xml string 时有正确的缩进关系
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            StringWriter stringWriter = new StringWriter();

            DOMSource domSource = new DOMSource(sourceNode);
            StreamResult streamResult = new StreamResult(stringWriter);

            transformer.transform(domSource, streamResult);
            String toString = stringWriter.toString();
            return toString;
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    public static String transformerToXmlString(NodeList nodeList) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < nodeList.getLength(); ++i) {
            Node item = nodeList.item(i);
            String xmlString = XmlDocumentParser.transformerToXmlString(item);
            stringBuilder.append(xmlString);
        }

        String toString = stringBuilder.toString();
        return toString;
    }

}