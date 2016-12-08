package part2;

import java.security.Key;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Iterator;

import javax.xml.crypto.AlgorithmMethod;
import javax.xml.crypto.KeySelector;
import javax.xml.crypto.KeySelectorException;
import javax.xml.crypto.KeySelectorResult;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.X509Data;

/**
 * NOTICE: This code was taken from an example from Sun/Oracle online here:
 * http://www.oracle.com/technetwork/articles/javase/dig-signature-api-140772.
 * html Legal Notice is here: https://www.oracle.com/legal/terms.html
 * 
 * NOTE: This software is free to use for non-commercial use. As this is being
 * Implemented for an academic assignment I'm interpreting that I can use the
 * software to help complete my assignment. If this is in violation of the terms
 * please contact the author and this source code will be removed.
 * 
 * Copyright © 2016, Oracle and/or its affiliates. All rights reserved.
 */
public class X509KeySelector extends KeySelector {

	/**
	 * Function to select the key based on the KeyInfo, Purpose, Algorithm, and
	 * XMLCrytpyoContext.
	 */
	public KeySelectorResult select(KeyInfo keyInfo, KeySelector.Purpose purpose, AlgorithmMethod method,
			XMLCryptoContext context) throws KeySelectorException {
		Iterator ki = keyInfo.getContent().iterator();
		while (ki.hasNext()) {
			XMLStructure info = (XMLStructure) ki.next();
			if (!(info instanceof X509Data))
				continue;
			X509Data x509Data = (X509Data) info;
			Iterator xi = x509Data.getContent().iterator();
			while (xi.hasNext()) {
				Object o = xi.next();
				if (!(o instanceof X509Certificate))
					continue;
				final PublicKey key = ((X509Certificate) o).getPublicKey();
				// Make sure the algorithm is compatible
				// with the method.
				if (algEquals(method.getAlgorithm(), key.getAlgorithm())) {
					return new KeySelectorResult() {
						public Key getKey() {
							return key;
						}
					};
				}
			}
		}
		throw new KeySelectorException("No key found!");
	}

	/**
	 * Checks if the algorithms are equal.
	 * @param algURI URI to the algorithm
	 * @param algName URI to the comparison algorithm
	 * @return flag if the algorithms match.
	 */
	boolean algEquals(String algURI, String algName) {
		if ((algName.equalsIgnoreCase("DSA") && algURI.equalsIgnoreCase(SignatureMethod.DSA_SHA1))
				|| (algName.equalsIgnoreCase("RSA") && algURI.equalsIgnoreCase(SignatureMethod.RSA_SHA1))) {
			return true;
		} else {
			return false;
		}
	}
}
