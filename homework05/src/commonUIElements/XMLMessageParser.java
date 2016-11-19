package commonUIElements;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Handles parsing a XML message.
 * 
 * @author Kyle
 */
public class XMLMessageParser {
	private static String clientRoot = "client";
	private static String serverRoot = "server";
	
	public static void parseXMLMessage(String xmlMessage) {
		InputSource is = new InputSource(new StringReader(xmlMessage));
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
			if(doc.getParentNode().getNodeName().equals(clientRoot)) {
				// TODO: Parse client message
			} else if(doc.getParentNode().getNodeName().equals(serverRoot)) {
				// TODO: Parse server message
			} else {
				System.err.println("Invalid message");
			}
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
