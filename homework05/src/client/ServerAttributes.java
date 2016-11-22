package client;

/**
 * Simple holder class for the server attributes needed to log into the server.
 * 
 * @author Kyle
 */
public class ServerAttributes {
	// address of the server
	public String serverName = "0.0.0.0";
	// the port number of the server
	public int port = 0;
	// Client trust store alias
	public String alias = "myKey";
	// password to the private key
	public char[] password;
}
