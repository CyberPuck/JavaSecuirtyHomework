package server;

public class ServerMain {

	public static void main(String[] args) {
		long timeout = 60000;
		CommandLineArgs parser = new CommandLineArgs();
		if (parser.parseCommandLineArgs(args)) {
			ServerSSLSocket socket = new ServerSSLSocket(parser.getPort());
			try {
				socket.startServer();
				long startTime = System.currentTimeMillis();
				while(System.currentTimeMillis() - startTime <= timeout) {
					// do nothing
				}
			} catch(Exception e) {
				System.err.println(e.getMessage());
			}
		}
	}

}
