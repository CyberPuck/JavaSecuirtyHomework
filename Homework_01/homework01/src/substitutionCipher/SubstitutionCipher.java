package substitutionCipher;

/**
 * Entry point for testing the substitution cipher. Takes in command line args,
 * parses them and executes decryption or encryption based on the options
 * provided.
 * 
 * @author Kyle
 *
 */
public class SubstitutionCipher {

	/**
	 * Given command line args, find the files to update and read, finally
	 * execute the desired encryption/decryption.
	 * 
	 * @param args
	 *            Command line arguments to parse.
	 */
	public static void main(String[] args) {
		// Parse command line arguments
		CommandLineArgs substiutionArgs = new CommandLineArgs();
		if (!substiutionArgs.parseCommandLineArgs(args)) {
			// failed to parse command line, exit out
			return;
		}
		// Start one of the following modes
		if (substiutionArgs.getMode() == CommandLineArgs.EncryptionMode.DECRYPT) {
			System.out.println("Decrypting...");
			// decrypt
			SubsitutionSuite.decrypt(substiutionArgs.getKeyPath(), substiutionArgs.getPlainTextPath(),
					substiutionArgs.getCipherTextPath());
		} else {
			System.out.println("Encrypting...");
			// encrypt
			SubsitutionSuite.encrypt(substiutionArgs.getKeyPath(), substiutionArgs.getPlainTextPath(),
					substiutionArgs.getCipherTextPath());
		}
	}

}
