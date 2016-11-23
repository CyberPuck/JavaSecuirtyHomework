package commonUIElements;

/**
 * Handles parsing and verifying the command line args for the application. This
 * includes: the location of the keystore
 * 
 * @author Kyle
 */
public class CommandLineArgs {
	// Location of the keystore
	private String keystoreLocation = ".keystore";
	// Location of the trust store
	private String trustStoreLocation = ".truststore";
	// name of the program running
	private String mainName = "unknown";
	// location of the settings file
	private String settingsLocation = ".settings";
	// location of the client settings file
	private String clientSettings = ".clientSettings";

	/**
	 * Constructor, allows the name of the application to be passed into the
	 * parser. The name will be displayed when help is displayed.
	 * 
	 * @param mainName
	 *            name of the running application
	 */
	public CommandLineArgs(String mainName) {
		this.mainName = mainName;
	}

	/**
	 * Parses the command line args, length is not really important but if a
	 * flag is detected without a corresponding value, the help is printed along
	 * with an error.
	 * 
	 * @param args
	 *            command line args to parse
	 * @return flag indicating a successfu parse
	 */
	public boolean parseCommandLineArgs(String[] args) {
		for (int i = 0; i < args.length; i++) {
			// make sure the string is lower case
			switch (args[i].toLowerCase()) {
			case "-k": // keystore
				keystoreLocation = args[++i];
				break;
			case "-s":
				settingsLocation = args[++i];
				break;
			case "-t":
				trustStoreLocation = args[++i];
				break;
			case "-c":
				clientSettings = args[++i];
				break;
			case "--help": // print help and stop execution
				printHelp();
				return false;
			default: // error
				System.err.println("Invalid command: " + args[i]);
				printHelp();
				return false;
			}
		}
		return true;
	}

	/**
	 * Prints the usage for command line args to System.out.
	 */
	private void printHelp() {
		System.out.println("\n\n" + mainName + "  ");
		System.out.println("Expected format: java " + mainName + "Main [<flag> <arg>]");
		System.out.println("Example: java " + mainName + "Main -k ../awesome.keystore");
		System.out.println("<-k>   Custom location of the key store, default=./.keystore");
		System.out.println("<-t>   Custom location of the trust store, default=./.truststore");
		System.out.println("<-s>   Custom location of the settings file, default=./.settings");
		System.out.println("<-s>   Custom location of the client settings file, default=./.clientSettings");

	}

	/**
	 * Gets the keystore location.
	 * 
	 * @return String representing the path to the keystore.
	 */
	public String getKeystoreLocation() {
		return keystoreLocation;
	}

	/**
	 * Get the location of the settings file.
	 * 
	 * @return String representing the path to the settings file.
	 */
	public String getSettings() {
		return settingsLocation;
	}

	/**
	 * Shortcut to setup the settings file name/location. Useful for
	 * establishing a custom default settings file.
	 * 
	 * @param settings
	 *            custom settings file location.
	 */
	public void setSettings(String settings) {
		this.settingsLocation = settings;
	}

	/**
	 * Get the location of the trust store.
	 * 
	 * @return String representing the path to the trust store.
	 */
	public String getTrustStoreLocation() {
		return trustStoreLocation;
	}

	/**
	 * Get the location of the client settings file.
	 * 
	 * @return String representing the path to the client settings file.
	 */
	public String getClientSettings() {
		return clientSettings;
	}
}
