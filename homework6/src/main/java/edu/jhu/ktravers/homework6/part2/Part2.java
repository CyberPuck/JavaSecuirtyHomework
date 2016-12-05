package edu.jhu.ktravers.homework6.part2;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;

import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xml.security.c14n.Canonicalizer;
import org.w3c.dom.Document;

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
			// C14N the unsigned file first, NOTE this is code from Part 1
			// TODO: Use built-in C14N?
			
			// create the signature factory
			// REMEMBER: DSA is required for signing
			// Cert will be stored in the document
			XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");
			

		} catch (Exception e) {
			System.err.println("Failed to read in the XML file: " + e.getMessage());
		}

	}

}
