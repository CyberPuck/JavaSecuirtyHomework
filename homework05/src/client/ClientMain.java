package client;

/**
 * Entry point for the client that communicates with the server.
 * 
 * @author Kyle
 */
public class ClientMain {

	/**
	 * Entry point for the application, takes in command line args and spins up
	 * the client.
	 * 
	 * @param args
	 *            paramters for starting the client.
	 */
	public static void main(String[] args) {
		// parse the command line args
		CommandLineArgs parser = new CommandLineArgs();
		if (parser.parseCommandLineArgs(args)) {
			// execute the client application
			ClientSSLSocket socket = new ClientSSLSocket(parser.getHostName(), parser.getPort());
			try {
				socket.run();
				long startTime = System.currentTimeMillis();
				while(System.currentTimeMillis() - startTime < 60000) {
					// do nothing
				}
			} catch(Exception e) {
				System.err.println("Socket err: " + e.getMessage());
			}
		}
	}

}
