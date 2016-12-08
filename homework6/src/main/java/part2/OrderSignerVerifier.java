package part2;

import java.io.File;
import java.io.FileOutputStream;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class will both sign order.xml into signedOrder.xml. It will also verify
 * signedOrder.xml, looking at the included certificate for verification.
 * 
 * @author Kyle
 */
public class OrderSignerVerifier {

	/**
	 * Handles the command line args and starts either the signing or verifying
	 * process.
	 * 
	 * @param args
	 *            CLI input
	 */
	public static void main(String[] args) {
		if (args.length != 5 && args.length != 2) {
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
		if (!path.contains("order.xml") && !path.contains("signedOrder.xml")) {
			System.err.println("Alert! Must input 'order.xml' or signedOrder.xml in path: " + path);
			System.exit(1);
		}
		// decide to verify or sign
		if (flag.equals("s")) {
			// sign
			System.out.println("Signing order.xml");
			String keyStore = args[i++];
			String password = args[i++];
			String alias = args[i++];
			// try to sign the XML document
			signXMLDocument(path, keyStore, password, alias);
		} else {
			// verify
			System.out.println("Verifying signedOrder.xml");
			if (verifySignature(path)) {
				System.out.println("signedOrder.xml if verified!");
			} else {
				System.out.println("signedOrder.xml has invalid signatures; reject it!");
			}
		}

	}

	/**
	 * Takes in a XML file, key store, password, and alias; signs the XML
	 * document's first Order and Commission attributes.
	 * 
	 * @param path
	 *            to the XML file
	 * @param keyStore
	 *            path to the key store
	 * @param password
	 *            password to the key store and DSA private key
	 * @param alias
	 *            of the DSA key
	 */
	public static void signXMLDocument(String path, String keyStore, String password, String alias) {
		// get private key
		PrivateKey key = KeyStoreAccess.getPrivateKey(keyStore, alias, password);
		// get certificate
		X509Certificate certificate = KeyStoreAccess.getCertificate(keyStore, alias, password);
		if (key == null || certificate == null) {
			// short cut exit, error occurred
			System.exit(1);
		}
		// setup the XML signature factory
		XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");
		// read in the XML document
		Document doc = readInXMLDoc(path);
		if (doc == null) {
			System.exit(1);
		}
		// generate a signature over the Order and Commission attributes
		if (signDocument(doc, fac, key, certificate)) {
			// print out the signed document on a successful signature
			printXmlDoc(doc);
			System.out.println("signedOrder.xml has been created");
		} else {
			System.err.println("Failed to sign " + path);
		}
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
	 * Signs the XML document. This is a larger function as separating the
	 * various functions looks like a pain.
	 * 
	 * @param doc
	 *            XML document to sign
	 * @param fac
	 *            signature factory to build XML signature
	 * @param key
	 *            private key to sign attributes
	 * @param certificate
	 *            to add to the XML file for verification
	 * @return flag indicating if the signature was a success
	 */
	public static boolean signDocument(Document doc, XMLSignatureFactory fac, PrivateKey key,
			X509Certificate certificate) {
		try {
			DigestMethod digestMethod = fac.newDigestMethod(DigestMethod.SHA1, null);
			C14NMethodParameterSpec spec = null;
			// java canonicalization defaults to C14N!
			CanonicalizationMethod cm = fac.newCanonicalizationMethod(CanonicalizationMethod.EXCLUSIVE_WITH_COMMENTS,
					spec);
			// signature is SHA1, highest Java will go for now
			SignatureMethod sm = fac.newSignatureMethod(SignatureMethod.DSA_SHA1, null);
			ArrayList<Transform> transformList = new ArrayList<Transform>();
			TransformParameterSpec transformSpec = null;
			// our signature is enveloped not enveloping
			Transform envTransform = fac.newTransform(Transform.ENVELOPED, transformSpec);
			transformList.add(envTransform);
			Reference ref = fac.newReference("", digestMethod, transformList, null, null);
			ArrayList<Reference> refList = new ArrayList<Reference>();
			refList.add(ref);
			// add the signature method, canonicalization method, and transforms
			// together
			SignedInfo signedInfo = fac.newSignedInfo(cm, sm, refList);
			// Get the Order attribute to sign
			NodeList orderList = doc.getDocumentElement().getElementsByTagName("Order");
			Node orderNode = orderList.item(0);
			DOMSignContext orderDom = new DOMSignContext(key, orderNode);
			// Get the Commission to sign
			NodeList commissionList = doc.getDocumentElement().getElementsByTagName("Commission");
			Node commissionNode = commissionList.item(0);
			DOMSignContext commissionDom = new DOMSignContext(key, commissionNode);
			// add certificate to XML
			KeyInfoFactory kif = fac.getKeyInfoFactory();
			// add the certificate and name to XML
			List<Object> x509DataList = new ArrayList<>();
			x509DataList.add(certificate.getSubjectX500Principal().getName());
			x509DataList.add(certificate);
			X509Data x509Data = kif.newX509Data(x509DataList);
			// key info to be added to XML with signature
			KeyInfo keyInfo = kif.newKeyInfo(Collections.singletonList(x509Data));
			// add both to the XML signature
			XMLSignature signature = fac.newXMLSignature(signedInfo, keyInfo);
			// sign Commission
			signature.sign(commissionDom);
			// sign Order
			signature.sign(orderDom);
			// signature didn't throw exception, mission accomplished
			return true;
		} catch (Exception e) {
			System.err.println("Failed to sign document: " + e.getMessage());
		}

		return false;
	}

	/**
	 * Verify the signatures in signedOrder.xml.
	 * 
	 * @param doc
	 *            XML document to check
	 * @param fac
	 *            signature factory for getting signature data
	 * @return flag indicating if the signature is valid
	 */
	public static boolean verifySignature(String path) {
		try {
			// setup the XML signature factory
			XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");
			Document doc = readInXMLDoc(path);
			if (doc == null) {
				System.exit(1);
			}
			// Find Signature element.
			NodeList nl = doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
			if (nl.getLength() == 0) {
				System.err.println("Cannot find Signature element");
				return false;
			}

			// Create a DOMValidateContext and specify a KeySelector
			// and document context.
			DOMValidateContext valContext = new DOMValidateContext(new X509KeySelector(), nl.item(1));

			// Unmarshal the XMLSignature, for verifier
			XMLSignature signatureVerifier = fac.unmarshalXMLSignature(valContext);

			// Validate the XMLSignature.
			if (!signatureVerifier.validate(valContext)) {

				// Check core validation status.
				System.err.println("Signature failed core validation");
				boolean sv = signatureVerifier.getSignatureValue().validate(valContext);
				System.out.println("signature validation status: " + sv);
				if (sv == false) {
					// Check the validation status of each Reference.
					Iterator i = signatureVerifier.getSignedInfo().getReferences().iterator();
					for (int j = 0; i.hasNext(); j++) {
						boolean refValid = ((Reference) i.next()).validate(valContext);
						System.out.println("ref[" + j + "] validity status: " + refValid);
					}
				}
			} else {
				return true;
			}
		} catch (Exception e) {
			System.err.println("Verification error: " + e.getMessage());
		}
		return false;
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
