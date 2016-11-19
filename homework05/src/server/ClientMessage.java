package server;

/**
 * Represents a message from a client. The formal name, message, and clearance
 * are inside this object. Allows the server to process and decide if the
 * message is forwarded and to which clients.
 * 
 * @author Kyle
 *
 */
public class ClientMessage {
	public String clientName;
	public String message;
	public int clearance;
}
