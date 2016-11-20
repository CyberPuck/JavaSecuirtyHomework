package commonUIElements;

/**
 * Object representing a message from either a server or client.
 * 
 * @author Kyle
 */
public class Message {
	public String senderName;
	public String message;
	public String signature;
	public int clearance;
	
	public Message(String name, String msg, String signature, int clearance) {
		this.senderName = name;
		this.message = msg;
		this.signature = signature;
		this.clearance = clearance;
	}
}
