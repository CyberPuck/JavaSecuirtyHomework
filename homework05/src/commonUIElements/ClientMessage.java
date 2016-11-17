package commonUIElements;

/**
 * Class that represents a message to a client from the server.
 * 
 * @author Kyle
 */
public class ClientMessage {
	private String message;
	private String signature;
	private int clearance;
	private String clientDN;

	/**
	 * Set the message, server signature, clearance level, and origination
	 * client distinguished name.
	 * 
	 * @param msg
	 *            message from client
	 * @param sign
	 *            signature of the server
	 * @param clearance
	 *            clearance level of the message
	 * @param clientDN
	 *            client DN that sent the message to the server originally
	 */
	public ClientMessage(String msg, String sign, int clearance, String clientDN) {
		this.message = msg;
		this.signature = sign;
		this.clearance = clearance;
		this.clientDN = clientDN;
	}

	/**
	 * Message to the client.
	 * 
	 * @return String message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Server signature.
	 * 
	 * @return signature of the message
	 */
	public String getSignature() {
		return signature;
	}

	/**
	 * Clearance level of the message.
	 * 
	 * @return int clearance level from 1 - 4
	 */
	public int getClearance() {
		return clearance;
	}

	/**
	 * Client DN that sent the message to the server
	 * 
	 * @return String clientDN
	 */
	public String getClientDN() {
		return clientDN;
	}
}
