package client;

/**
 * Handles parsing and verifying the command line args for the client
 * application. This includes: the location of the keystore
 * 
 * @author Kyle
 */
public class CommandLineArgs {
	// Location of the keystore
	private String keystoreLocation = ".keystore";

	/**
	 * Default constructor, all local variables are set before construction.
	 */
	public CommandLineArgs() {
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
		System.out.println("\n\nClient  ");
		System.out.println("This client can take no input which results in assuming the keystore is located at: ./.keystore");
		System.out.println("Expected format: java client [<flag> <arg>]");
		System.out.println("Example: java ClientMain -k ../awesome.keystore");
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
