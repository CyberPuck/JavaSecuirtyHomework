package commonUIElements;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.logging.LogRecord;
import java.util.logging.XMLFormatter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Class to override existing XML logging functionality to display in a manner
 * that makes more sense for the server.
 * 
 * @author Kyle
 */
public class XMLLogFormatter extends XMLFormatter {

	// date formatter
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd: hh:mm:ss");

	/**
	 * Handles logging a message in XML format.
	 */
	@Override
	public String format(LogRecord logRecord) {

		try {
			DocumentBuilderFactory xmlFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = xmlFactory.newDocumentBuilder();
			Document doc = builder.newDocument();
			// root is log
			Element root = doc.createElement("log");
			doc.appendChild(root);
			// add the level
			Element level = doc.createElement("level");
			level.appendChild(doc.createTextNode(logRecord.getLevel().toString()));
			root.appendChild(level);
			// add in the timestamp
			Element timestamp = doc.createElement("date");
			timestamp.appendChild(doc.createTextNode(dateFormatter.format(logRecord.getMillis())));
			root.appendChild(timestamp);
			// add in the message
			Element message = doc.createElement("message");
			message.appendChild(doc.createTextNode(logRecord.getMessage()));
			root.appendChild(message);
			// create the source object
			DOMSource source = new DOMSource(root);
			// transform the DOM
			TransformerFactory factory = TransformerFactory.newInstance();
			factory.setAttribute("indent-number", 2);
			Transformer transformer = factory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			StreamResult result = new StreamResult(new StringWriter());
			transformer.transform(source, result);
			String xmlString = result.getWriter().toString();
			System.out.println(xmlString);
			return xmlString;
		} catch (Exception e) {
			System.err.println("XML formatter error" + e.getMessage());
		}
		// just in case, log the message
		return "<log>" + logRecord.getMessage() + "</log>";
	}
}
