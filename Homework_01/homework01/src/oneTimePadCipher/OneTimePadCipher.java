package oneTimePadCipher;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

import utilities.FileIO;

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
			String originalPlainText = FileIO.readFile(plainTextFile);
			// generate the secure random number generator
			SecureRandom rng = SecureRandom.getInstance("SHA1PRNG");
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
			logger.log(Level.INFO, byteArrayToString(key));
			logger.log(Level.INFO, "---Original Plain Text---");
			logger.log(Level.INFO, originalPlainText);
			logger.log(Level.INFO, "---Cipher Text---");
			logger.log(Level.INFO, new String(cipherTextArray));
			logger.log(Level.INFO, "---Decrypted Text---");
			logger.log(Level.INFO, new String(plainTextArray));
		} catch (IOException e) {
			System.err.println("Error reading the file: " + e.getMessage());
		} catch (NoSuchAlgorithmException err) {
			System.err.println("Error getting ScureRandom algorithm");
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

	/**
	 * Converts a byte array to a string of binary data.
	 * 
	 * @param array
	 *            byte array to convert
	 * @return binary represented string of byte array
	 */
	private static String byteArrayToString(byte[] array) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < array.length; i++) {
			int temp = array[i];
			String data = String.format("%8s", Integer.toBinaryString(temp)).replace(' ', '0');
			if (data.length() > 8) {
				// handling java and its integer crazyness
				data = data.substring(data.length() - 8, data.length());
			}
			builder.append(data);
			if (i % 8 == 0 && i != 0) {
				builder.append("\n");
			}
		}
		return builder.toString();
	}
}
