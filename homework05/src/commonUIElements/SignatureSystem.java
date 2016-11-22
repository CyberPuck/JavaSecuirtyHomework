package commonUIElements;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.SignedObject;
import java.util.Base64;

/**
 * Simple signature system that will: sign a given string and return the BASE64
 * of the signature and verify if a BASE64 signature signed a string
 * 
 * @author Kyle
 */
public class SignatureSystem {
	private static String SIGNING_ALGORITHM = "SHA256WithRSA";

	/**
	 * Signs a message with a given private key and message. The returned string
	 * is a BASE64 encoding of the signature.
	 * 
	 * @param message
	 *            needs to be signed
	 * @param privateKey
	 *            key to sign message
	 * @return BASE64 of message if signed, or null if failure
	 */
	public static String signMessage(String message, PrivateKey privateKey) {
		try {
			Signature signAlgorithm = Signature.getInstance(SIGNING_ALGORITHM);
			SignedObject so = new SignedObject(message, privateKey, signAlgorithm);
			return new String(Base64.getEncoder().encode(so.getSignature()));
		} catch (InvalidKeyException | SignatureException | IOException | NoSuchAlgorithmException e) {
			System.err.println("Signing error: " + e.getMessage());
		}
		return null;
	}

	/**
	 * Verifies the signature of a given message, with the help of the alias to
	 * the public cert stored in the key store.
	 * 
	 * @param signature
	 *            supposed BASE64 signature of the message
	 * @param message
	 *            message to compare
	 * @param certAlias
	 *            name of public cert to use to unlock the key store
	 * @param keyStore
	 *            contains the public key of the supposed signature
	 * @return flag indicating if the signature appears to be valid
	 */
	public static boolean verifySignature(String signature, String message, String certAlias, KeyStore keyStore) {
		Signature signAlgorithm;
		try {
			signAlgorithm = Signature.getInstance(SIGNING_ALGORITHM);
			signAlgorithm.initVerify(keyStore.getCertificate(certAlias));
			signAlgorithm.update(message.getBytes());
			return signAlgorithm.verify(Base64.getDecoder().decode(signature));
		} catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException | KeyStoreException e) {
			System.err.println("Verifying error: " + e.getMessage());
		}
		
		return false;
	}
}
