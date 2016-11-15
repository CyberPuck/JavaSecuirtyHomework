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
	private String mainName = "unknown";

	/**
	 * Constructor, allows the name of the application to be passed into the
	 * parser. The name will be displayed when help is displayed.
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
		System.out.println(
				mainName + " can take no input which results in assuming the keystore is located at: ./.keystore");
		System.out.println("Expected format: java " + mainName + "Main [<flag> <arg>]");
		System.out.println("Example: java " + mainName + "Main -k ../awesome.keystore");
		System.out.println("<-k>   Custom location of the keystore, default=./.keystore");

	}

	/**
	 * Gets the keystore location.
	 * 
	 * @return String representing the path to the keystore.
	 */
	public String getKeystoreLocation() {
		return keystoreLocation;
	}

}
