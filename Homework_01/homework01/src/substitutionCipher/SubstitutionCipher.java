package substitutionCipher;

public class SubstitutionCipher {

	public static void main(String[] args) {
		// Parse command line arguments
		CommandLineArgs substiutionArgs = new CommandLineArgs();
		substiutionArgs.parseCommandLineArgs(args);
		// Start one of the following modes
		if (substiutionArgs
				.getMode() == CommandLineArgs.EncryptionMode.DECRYPT) {
			// decrypt
			SubsitutionSuite.decrypt(substiutionArgs.getKeyPath(),
					substiutionArgs.getPlainTextPath(),
					substiutionArgs.getCipherTextPath());
		} else {
			// encrypt
			SubsitutionSuite.encrypt(substiutionArgs.getKeyPath(),
					substiutionArgs.getPlainTextPath(),
					substiutionArgs.getCipherTextPath());
		}
	}

}
