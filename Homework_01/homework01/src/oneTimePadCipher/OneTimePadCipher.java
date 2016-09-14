package oneTimePadCipher;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

import utilities.Utilities;

public class OneTimePadCipher {

	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("Please enter the file name to encrypt");
			return;
		}
		// get the file object
		File plainTextFile = new File(args[0]);
		// try to read in the file
		try {
			String originalPlainText = Utilities.readFile(plainTextFile);
			// generate the secure random number generator
			SecureRandom rng = new SecureRandom();
			// convert string to byte array
			byte[] originalPlainByteArray = originalPlainText.getBytes();
			// ensure the key length matches the plain text
			byte[] key = new byte[originalPlainByteArray.length];
			// generate the key
			rng.nextBytes(key);
			// encrypt the data
			byte[] cipherTextArray = encryptByteArray(originalPlainByteArray, key);
			// decrypt the data
			byte[] plainTextArray = decryptByteArray(cipherTextArray, key);
			// print out results
			Logger logger = Logger.getLogger(OneTimePadCipher.class.getName());
			logger.log(Level.INFO, "---KEY---");
			logger.log(Level.INFO, key.toString());
			logger.log(Level.INFO, "---Plain Text---");
			logger.log(Level.INFO, originalPlainText);
			logger.log(Level.INFO, "---Cipher Text---");
			logger.log(Level.INFO, new String(cipherTextArray));
			logger.log(Level.INFO, "---Decrypted Text---");
			logger.log(Level.INFO, new String(plainTextArray));
		} catch (IOException e) {
			System.err.println("Error reading the file: " + e.getMessage());
		}
	}

	/**
	 * Encrypts the given byte array plain text with the provided key by XORing
	 * plain text with key.
	 * 
	 * @param plainText
	 *            byte array to be encrypted
	 * @param key
	 *            byte array must be same length as plainText
	 * @return cipher text
	 */
	private static byte[] encryptByteArray(byte[] plainText, byte[] key) {
		byte[] cipherText = new byte[plainText.length];
		for (int i = 0; i < plainText.length; i++) {
			cipherText[i] = (byte) (plainText[i] ^ key[i]);
		}
		return cipherText;
	}

	/**
	 * Decrypts the given cipher text byte array with the provided key by XORing
	 * cipher text with key.
	 * 
	 * @param cipherText
	 *            byte array to be decrypted
	 * @param key
	 *            byte array must be same length as cipherText
	 * @return plain text
	 */
	private static byte[] decryptByteArray(byte[] cipherText, byte[] key) {
		byte[] plainText = new byte[cipherText.length];
		for (int i = 0; i < cipherText.length; i++) {
			plainText[i] = (byte) (cipherText[i] ^ key[i]);
		}
		return plainText;
	}
}
