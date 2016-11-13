package keyStore;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Arrays;

/**
 * Simple wrapper class to access the keystore. Given a password and path, a
 * KeyStore object is returned if it is unlocked.
 * 
 * @author Kyle
 */
public class KeyStoreAccessor {
	// keystore type, using JKS as it is the Java solution
	private final static String KEYSTORE_TYPE = "JKS";
	
	public static KeyStore getKeyStore(final char[] password, String keyStorePath) {
		KeyStore keyStore = null;
		try {
			keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
			// try to read in the key store
			System.out.println("Working Directory = " +
		              System.getProperty("user.dir"));
			try(FileInputStream fis = new FileInputStream(keyStorePath)) {
				keyStore.load(fis, password);
			} catch(IOException e) {
				keyStore = null;
				System.err.println("Failed to open keystore");
			} catch(NoSuchAlgorithmException|CertificateException e) {
				keyStore = null;
				System.err.println("Failed handle keystore: " + e.getMessage());
			}
		} catch(KeyStoreException e) {
			System.err.println("Error standing up the keystore: " + e.getMessage());
		}
		// Zero out password no matter what
		Arrays.fill(password, ' ');
		
		return keyStore;
	}
}
