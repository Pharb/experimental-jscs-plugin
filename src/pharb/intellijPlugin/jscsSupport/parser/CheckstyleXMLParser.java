package pharb.intellijPlugin.jscsSupport.parser;

import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;


public class CheckstyleXMLParser {

    public static List<MessageContainer> parse(final String rawXML) {
        Document document = getDocument(rawXML);
        List<MessageContainer> result = new ArrayList<>();

        NodeList fileNodes = document.getDocumentElement().getChildNodes();

        for (int i = 0; i < fileNodes.getLength(); i++) {
            result.addAll(processFileNode(fileNodes.item(i)));
        }

        return result;
    }

    private static List<MessageContainer> processFileNode(Node fileNode) {
        List<MessageContainer> result = new ArrayList<>();

        if (fileNode instanceof Element) {
            String fileName = fileNode.getAttributes().getNamedItem("name").getNodeValue();
            NodeList errorNodes = fileNode.getChildNodes();

            for (int j = 0; j < errorNodes.getLength(); j++) {
                Node errorNode = errorNodes.item(j);
                if (errorNode instanceof Element) {
                    NamedNodeMap attributes = errorNode.getAttributes();

                    String message = attributes.getNamedItem("message").getNodeValue();
                    int line = Integer.parseInt(attributes.getNamedItem("line").getNodeValue());
                    int column = Integer.parseInt(attributes.getNamedItem("column").getNodeValue());
                    String severity = attributes.getNamedItem("severity").getNodeValue();
                    String source = attributes.getNamedItem("source").getNodeValue();


                    System.out.printf("Found in %s line %s column %s message %s \n", fileName, line, column, message);

                    result.add(new MessageContainer(fileName, message, line, column, severity, source));
                }
            }
        }
        return result;
    }

    private static Document getDocument(String rawXML) {
        Document document = null;
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            document = builder.parse(new InputSource(new StringReader(rawXML)));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return document;
    }
}
