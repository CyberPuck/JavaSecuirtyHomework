package substitutionCipher;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import utilities.Utilities;

/**
 * Handles the encryption and decryption based on a provided key.
 * 
 * @author Cyber_Puck
 */
public class SubsitutionSuite {

	/**
	 * Handles a substitution cipher encryption, the cipher text file will be
	 * overwritten.
	 * 
	 * @param keyPath
	 *            path to the key file
	 * @param plainTextPath
	 *            path to the plain text file
	 * @param cipherTextPath
	 *            path to the cipher text file to be overwritten
	 * @return flag indicating if an error occurred during encryption
	 */
	public static boolean encrypt(String keyPath, String plainTextPath,
			String cipherTextPath) {
		File keyFile = new File(keyPath);
		File plainTextFile = new File(plainTextPath);
		File cipherTextFile = new File(cipherTextPath);

		if (verifyFiles(keyFile, plainTextFile, cipherTextFile)) {
			try {
				String key = Utilities.readFile(keyFile);
				String plainText = Utilities.readFile(plainTextFile);
				// set all text to upper case, just in case
				plainText = plainText.toUpperCase();
				StringBuilder cipherTextBuilder = new StringBuilder();
				// run through each character and encrypt
				for (char c : plainText.toCharArray()) {
					// calculate the index value of the character (A = 0 to Z =
					// 26)
					int index = ((int) c) - (int) 'A';
					// ensure the current character is supported by the cipher
					if (index >= 0 && index <= key.length()) {
						c = key.charAt(index);
					}
					cipherTextBuilder.append(c);
				}

				// log out the results
				Logger logger = Logger
						.getLogger(SubsitutionSuite.class.getName());
				logger.log(Level.INFO, "---KEY---");
				logger.log(Level.INFO, key);
				logger.log(Level.INFO, "---PLAIN TEXT---");
				logger.log(Level.INFO, plainText);
				logger.log(Level.INFO, "---CIPHER TEXT---");
				logger.log(Level.INFO, cipherTextBuilder.toString());

				// write the cipher text to file
				Utilities.writeFile(cipherTextFile, cipherTextBuilder.toString());

			} catch (IOException e) {
				System.err.println(
						"Error occured during file I/O: " + e.getMessage());
				return false;
			}
		} else {
			return false;
		}

		return true;
	}

	/**
	 * Decrypts the cipher text based on the substitution key provided.
	 * 
	 * @param keyPath
	 *            path to the key file
	 * @param plainTextPath
	 *            path to the plain text file (will be overwritten)
	 * @param cipherTextPath
	 *            path to cipher text file
	 * @return flag indicating if the operation was successful
	 */
	public static boolean decrypt(String keyPath, String plainTextPath,
			String cipherTextPath) {
		File keyFile = new File(keyPath);
		File plainTextFile = new File(plainTextPath);
		File cipherTextFile = new File(cipherTextPath);

		if (verifyFiles(keyFile, cipherTextFile, plainTextFile)) {
			try {
				String key = Utilities.readFile(keyFile);
				String cipherText = Utilities.readFile(cipherTextFile);
				// set all text to upper case, just in case
				cipherText = cipherText.toUpperCase();
				StringBuilder plainTextBuilder = new StringBuilder();
				// run through each character and encrypt
				for (char c : cipherText.toCharArray()) {
					// calculate the index of the char in the key
					int index = key.indexOf(c);
					// ensure the current character is supported by the cipher
					if (index >= 0 && index < key.length()) {
						// the index in the array is the offset from ASCII 'A'
						c = (char) (index + (int) 'A');
					}

					plainTextBuilder.append(c);
				}
				// log out the results
				Logger logger = Logger
						.getLogger(SubsitutionSuite.class.getName());
				logger.log(Level.INFO, "---KEY---");
				logger.log(Level.INFO, key);
				logger.log(Level.INFO, "---CIPHER TEXT---");
				logger.log(Level.INFO, cipherText);
				logger.log(Level.INFO, "---PLAIN TEXT---");
				logger.log(Level.INFO, plainTextBuilder.toString());
				// write the plain text to file
				Utilities.writeFile(plainTextFile, plainTextBuilder.toString());

			} catch (IOException e) {
				System.err.println(
						"Error occured during file I/O: " + e.getMessage());
				return false;
			}
		} else {
			return false;
		}

		return true;
	}

	/**
	 * Verify files exist and the output file can be created if it isn't.
	 * 
	 * @param keyFile
	 *            key file
	 * @param inputFile
	 * @param outputFile
	 * @return
	 */
	private static boolean verifyFiles(File keyFile, File inputFile,
			File outputFile) {
		// check the files
		if (!keyFile.exists()) {
			System.err.println("Key file does not exist");
			return false;
		}
		if (!inputFile.exists()) {
			System.err.println("Input file does not exist");
		}
		// create the cipher text file if it doesn't exist
		if (!outputFile.exists()) {
			try {
				outputFile.createNewFile();
			} catch (IOException e) {
				System.err.println("Failed to create output file");
				System.err.println("Error: " + e.getMessage());
				return false;
			}
		}

		return true;
	}
}
