package part2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

/**
 * This class will both sign order.xml into signedOrder.xml. It will also verify
 * signedOrder.xml, looking at the included certificate for verification.
 * 
 * @author Kyle
 */
public class OrderSignerVerifier {

	public static void main(String[] args) {
		if (args.length != 5 || args.length != 2) {
			System.err.println("Invalid number of command line arguments!");
			System.out.println(
					"Usage:  OrderSignerVerifier s <order.xml path> <key store path> <keystore password> <key alias>");
			System.out.println("Usage:  OrderSignerVerifier v <signedOrder.xml path>");
			System.out.println("<signFlag> is /'s/' or /'v/', s=sign file, v=verify file");
			System.out.println("NOTE:  keystore password MUST match key password");
			System.exit(1);
		}
		// get initial variables
		int i = 0;
		String flag = args[i++];
		String path = args[i++];
		// verify order.xml is in path
		if (path.contains("order.xml") || path.contains("signedOrder.xml")) {
			System.err.println("Alert! Must input /'order.xml' in path: " + path);
			System.exit(1);
		}
		// decide to verify or sign
		if (flag.equals("s")) {
			// sign
			System.out.println("Signing order.xml");
			String keyStore = args[i++];
			String password = args[i++];
			String alias = args[i++];

		} else {
			// verify
			System.out.println("Verify signedOrder.xml");
		}

	}

	public static void signXMLDocument(String path, String keyStore, String password, String alias) {
		// get private key
		PrivateKey privateKey = KeyStoreAccess.getPrivateKey(keyStore, alias, password);
		// get certificate
		X509Certificate certificate = KeyStoreAccess.getCertificate(keyStore, alias, password);
		if (privateKey == null || certificate == null) {
			// short cut exit, error occurred
			System.exit(1);
		}
		// setup the XML signature factory
		XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");
		// read in the XML document
		Document doc = readInXMLDoc(path);
		if(doc == null) {
			System.exit(1);
		}
		// generate a signature over the Order and Commission attributes
		
	}

	/**
	 * Reads in the XML file from a path and returns the Document representing
	 * it.
	 * 
	 * @param path
	 *            to the XML file order.xml
	 * @return Document or null if an error occurred
	 */
	public static Document readInXMLDoc(String path) {
		try {
			// read in the XML file and parse it
			File file = new File(path);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			return builder.parse(file);
		} catch (Exception e) {
			System.err.println("Failed to generate XML doc: " + e.getMessage());
		}
		return null;
	}

	/**
	 * Print out the XML file as signedOrder.xml.
	 * 
	 * @param doc
	 *            XML document to print out.
	 */
	public static void printXmlDoc(Document doc) {
		try {
			// write to signedOrder.xml
			FileOutputStream fos = new FileOutputStream("signedOrder.xml");
			// create transformer to write out file
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer trans = tf.newTransformer();
			// make the output more readable
			trans.setOutputProperty(OutputKeys.INDENT, "yes");
			trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			trans.transform(new DOMSource(doc), new StreamResult(fos));
		} catch (Exception e) {
			System.err.println("Failed to write out signedOrder.xml: " + e.getMessage());
		}
	}
}
