package tripwire;

import java.io.File;

/**
 * Tripwire simulator entry point.
 * 
 * @author Kyle
 */
public class FolderChecker {

	/**
	 * Entry point fir the Tripwire simulator. Command line args are read in,
	 * and the simulator is started.
	 * 
	 * @param args
	 *            indicates the mode: scan or check, password, and folder name.
	 */
	public static void main(String[] args) {
		// verify the command line args are good
		if (!checkCommandLineArgs(args)) {
			printHelp();
			return;
		}
		// enter the arguments into the simulator
		String scanMode = args[0];
		String password = args[1];
		String path = args[2];
		// TODO: Code up the simulator
	}

	/**
	 * Checks the command line arguments to verify they are valid.
	 * 
	 * @param args
	 *            Command line args to be checked.
	 * @return flag indicating if the command line args are valid.
	 */
	private static boolean checkCommandLineArgs(String[] args) {
		// check all args are present
		if (args.length != 3) {
			System.err.println("Incorrect number of command line args");
		} else if (!args[0].equalsIgnoreCase("-scan") && !args[0].equalsIgnoreCase("-scan")) {
			// verify first command line arg is scan or check mode flag
			System.err.println("First command line arg must be '-scan' or '-check'");
		} else if (!new File(args[2]).isDirectory()) {
			// verify path from the third command line exists
			System.err.println("Third command line arg must be a valid directory");
		} else {
			// command line args pass initial check
			return true;
		}
		return false;
	}

	/**
	 * Prints a simple help dialog if the command line args were found to be
	 * incorrect.
	 */
	private static void printHelp() {
		System.out.println("Tripwire simulator help.");
		System.out.println("Usage: FolderChecker <option> <password> <folderName>");
		System.out.println("<option>: either -scan or -check");
		System.out.println("\tScan: scans the files in the folders and generates hashs");
		System.out.println("\tCheck: uses the hashs from -scan to see if files have been modified and flags them");
		System.out.println("<password>: encrypts the hash results from the file");
		System.out.println("<folderName>: name of the folder to scan or check using the .folderCheck XML file");
	}
}
