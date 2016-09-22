package substitutionCipher;

/**
 * Parses, verifies, and stores the command line arguments.
 * 
 * @author Cyber_Puck
 */
public class CommandLineArgs {
	public enum EncryptionMode {
		DECRYPT, ENCRYPT
	};

	// mode entered in command line
	private EncryptionMode mode;
	// paths to the key, encryption, and plain text files
	private String keyPath;
	private String plainTextPath;
	private String cipherTextPath;
	// expected number of command line args
	private static int EXPECTED_NUMBER_OF_ARGS = 4;

	public CommandLineArgs() {
		mode = EncryptionMode.DECRYPT;
		keyPath = "";
		plainTextPath = "";
		cipherTextPath = "";
	};

	/**
	 * Parses the command line arguments. Will return false if the data entered
	 * is malformed or invalid.
	 * 
	 * @param args
	 *            Array of strings representing the arguments
	 * @return flag indicating if the args were parsed
	 */
	public boolean parseCommandLineArgs(String[] args) {
		boolean returnValue = true;
		if (args.length != EXPECTED_NUMBER_OF_ARGS) {
			System.err.println("Expeceted 4 arguments, received: " + args.length);
			returnValue = false;
		} else {
			// parse the mode
			if (args[0].equals("e")) {
				mode = EncryptionMode.ENCRYPT;
			} else if (args[0].equals("d")) {
				mode = EncryptionMode.DECRYPT;
			} else {
				System.err.println("The first argument must be an 'e' or 'd', got: " + args[0]);
				returnValue = false;
			}

			// add in the paths
			keyPath = args[1];
			if (mode.equals(EncryptionMode.ENCRYPT)) {
				plainTextPath = args[2];
				cipherTextPath = args[3];
			} else {
				plainTextPath = args[3];
				cipherTextPath = args[2];
			}
		}

		// print help if a parsing error occurred
		if (!returnValue) {
			printHelp();
		}
		return returnValue;
	}

	private static void printHelp() {
		System.out.println("\n\nSubstitutionCipher  ");
		System.out.println("This application requires 4 command line arguements.");
		System.out.println("Expected format:  java substitutionCipher <mode> <key> <plain text> <cipher text>.");
		System.out.println("Example: java substitutionCipher e ./key ./plainText ./cipherText.");
		System.out.println("<mode>:        Either an 'e' (encryption) or 'd' (decryption).");
		System.out.println("<key>:         Path to the key file.");
		System.out.println("<plain text>:  Path to plain text file.");
		System.out.println("<cipher text>: Path to cipher text file.\n");
		System.out.println("NOTE:  <plain text> and <cipher text> change order in decryption mode");
		System.out.println("NOTE:  Paths can be relative or absolute");
	}

	/**
	 * Gets the encryption mode enum read in.
	 * 
	 * @return EncryptionMode
	 */
	public EncryptionMode getMode() {
		return mode;
	}

	/**
	 * Gets the key path entered in command line .
	 * 
	 * @return key path
	 */
	public String getKeyPath() {
		return keyPath;
	}

	/**
	 * Gets the plain text entered in command line.
	 * 
	 * @return plain text path
	 */
	public String getPlainTextPath() {
		return plainTextPath;
	}

	/**
	 * Gets the cipher text entered in command line.
	 * 
	 * @return cipher text path
	 */
	public String getCipherTextPath() {
		return cipherTextPath;
	}
}
