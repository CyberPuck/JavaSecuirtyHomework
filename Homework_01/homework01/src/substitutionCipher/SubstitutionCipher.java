package substitutionCipher;

public class SubstitutionCipher {

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
