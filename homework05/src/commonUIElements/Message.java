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
	
	/**
	 * 
	 * @param name
	 * @param msg
	 * @param signature
	 * @param clearance
	 */
	public Message(String name, String msg, String signature, int clearance) {
		this.senderName = name;
		this.message = msg;
		this.signature = signature;
		this.clearance = clearance;
	}
}
