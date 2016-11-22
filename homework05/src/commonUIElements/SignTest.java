package commonUIElements;

import java.security.KeyStore;
import java.security.PrivateKey;

import keyStore.KeyStoreAccessor;

public class SignTest {

	public static void main(String[] args) {
		// test those mother fucking signing functions
		KeyStore ks = KeyStoreAccessor.getKeyStore("serverPassword".toCharArray(), "serverKeystore.jks");
		KeyStore ts = KeyStoreAccessor.getKeyStore("clientPassword".toCharArray(), "clientTruststore.jks");
		// get the server's private key
		try {
			PrivateKey ps = (PrivateKey)ks.getKey("server", "serverPassword".toCharArray());
			String message = "Test";
			String signature = SignatureSystem.signMessage(message, ps);
			if (SignatureSystem.verifySignature(signature, message, "server", ts)) {
				System.out.println("Success");
			} else {
				System.out.println("Failure");
			}
		} catch (Exception e) {
			System.out.println("Failed to get key " + e.getMessage());
		}
	}

}
