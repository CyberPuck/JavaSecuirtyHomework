package server;

/**
 * Handles parsing and verifying the command line args for the server
 * application. This includes: port number, clearance level.
 * 
 * @author Kyle
 */
public class CommandLineArgs {
	// Port of the server, defaults to 8080
	private int port = 8080;

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
			case "-p": // port
				try {
					port = Integer.parseInt(args[++i]);
				} catch (NumberFormatException e) {
					System.out.println(args[i] + " must be a valid integer");
					printHelp();
					return false;
				}
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
		System.out.println("\n\nServer  ");
		System.out.println("This server can take no input which results in a connection to 127.0.0.1:8080");
		System.out.println("Expected format: java server [<flag> <arg>]");
		System.out.println("Example: java ServerMain -p 1234");
		System.out.println("<-p>:  Port of the server, default=8080");

	}

	/**
	 * Gets the port number to the server.
	 * 
	 * @return integer representing the port number.
	 */
	public int getPort() {
		return port;
	}
}
