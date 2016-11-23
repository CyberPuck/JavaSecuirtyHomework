package commonUIElements;

/**
 * Object representing a message from either a server or client.
 * 
 * @author Kyle
 */
public class Message {
	// name of the send of the message
	public String senderName;
	// message to transmit
	public String message;
	// alias of the certificate/private key combo
	public String alias;
	// Signature of the message
	public String signature;
	// clearance of the message
	public int clearance;
	// indicates if the message is an error
	public boolean error = false;
	// indicates the client with the name should be killed
	public boolean kill = false;

	/**
	 * Simple message container.
	 * 
	 * @param name
	 *            name of alias of the message sender
	 * @param msg
	 *            string
	 * @param signature
	 *            BASE64 encoded signature of msg
	 * @param clearance
	 *            level (1-4)
	 */
	public Message(String name, String msg, String signature, int clearance) {
		this.senderName = name;
		this.message = msg;
		this.signature = signature;
		this.clearance = clearance;
	}
}
