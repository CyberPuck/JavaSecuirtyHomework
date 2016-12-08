package part2;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

/**
 * Handles key store access for XML signature.
 * 
 * @author Kyle
 */
public class KeyStoreAccess {
	/**
	 * Gets the private key in the key store for signing.
	 * 
	 * @param keyStore
	 *            path to key store to access
	 * @param alias
	 *            name of key alias
	 * @param password
	 *            to both the key store AND private key
	 * @return PrivateKey or null if an error occurs
	 */
	public static PrivateKey getPrivateKey(String keyStore, String alias, String password) {
		try {
			// Unlock key store
			KeyStore ks = KeyStore.getInstance("JKS");
			ks.load(new FileInputStream(keyStore), password.toCharArray());
			// load the private key
			return (PrivateKey) ks.getKey(alias, password.toCharArray());
		} catch (Exception e) {
			System.err.println("Failed to unlock key store: " + e.getMessage());
		}
		return null;
	}

	/**
	 * Gets the X509 certificate from the key store.
	 * 
	 * @param keyStore
	 *            path to key store
	 * @param alias
	 *            name of certificate alias
	 * @param password
	 *            to the key store
	 * @return X509Certificate or null if an error occurs
	 */
	public static X509Certificate getCertificate(String keyStore, String alias, String password) {
		try {
			// Unlock key store
			KeyStore ks = KeyStore.getInstance("JKS");
			ks.load(new FileInputStream(keyStore), password.toCharArray());
			// load the certificate
			return (X509Certificate) ks.getCertificate(alias);
		} catch (Exception e) {
			System.err.println("Failed to get certificate: " + e.getMessage());
		}
		return null;
	}
}
