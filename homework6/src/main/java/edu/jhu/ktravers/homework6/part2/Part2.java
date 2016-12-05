package edu.jhu.ktravers.homework6.part2;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.ArrayList;

import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Handles signing XML files. NOTE All passwords are not cleared during this
 * program as they are stored in strings.
 * 
 * @author Kyle
 */
public class Part2 {

	/**
	 * Program entry point for signing XML files.
	 * 
	 * @param args
	 *            <input file> <key store> <keystore password> <key alias>
	 */
	public static void main(String[] args) {
		// NOTE: JVM told me to
		org.apache.xml.security.Init.init();

		if (args.length != 4) {
			System.err.println("Invalid number of command line arguments!");
			System.out.println("Usage:  Part2 <order.xml path> <key store path> <keystore password> <key alias>");
			System.out.println("NOTE:  keystore password MUST match key password");
			System.exit(1);
		}

		try {
			String path = args[0];
			String keyStore = args[1];
			String password = args[2];
			String alias = args[3];
			// Unlock key store
			KeyStore ks = KeyStore.getInstance("JKS");
			ks.load(new FileInputStream(keyStore), password.toCharArray());
			PrivateKey key = (PrivateKey) ks.getKey(alias, password.toCharArray());
			// read in the XML file and parse it
			File file = new File(path);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(file);
			// test to see if we get the order
			Node order = doc.getDocumentElement().getElementsByTagName("order").item(0);
			// create the signature context
			DOMSignContext dsc = new DOMSignContext(key, doc.getDocumentElement().getElementsByTagName("order").item(0));
			// create the signature factory
			// REMEMBER: DSA is required for signing
			// Cert will be stored in the document
			XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");
			// SHA1 digest, note SHA1 is being retired after 2016 by most CAs!
			DigestMethod digestMethod = fac.newDigestMethod(DigestMethod.SHA1, null);
			C14NMethodParameterSpec spec = null;
			// java canonicalization defaults to C14N!
			CanonicalizationMethod cm = fac.newCanonicalizationMethod(CanonicalizationMethod.EXCLUSIVE_WITH_COMMENTS,
					spec);
			SignatureMethod sm = fac.newSignatureMethod(SignatureMethod.DSA_SHA1, null);
			ArrayList<Transform> transformList = new ArrayList<Transform>();
			TransformParameterSpec transformSpec = null;
			// our signature is enveloped not enveloping
			Transform envTransform = fac.newTransform(Transform.ENVELOPED, transformSpec);
			transformList.add(envTransform);
			Reference ref = fac.newReference("", digestMethod, transformList, null, null);
			ArrayList<Reference> refList = new ArrayList<Reference>();
			refList.add(ref);
			SignedInfo si = fac.newSignedInfo(cm, sm, refList);

		} catch (Exception e) {
			System.err.println("Failed to read in the XML file: " + e.getMessage());
		}

	}

}
