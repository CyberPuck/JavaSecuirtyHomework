package commonUIElements;

/**
 * All UI elements that handle the socket need to implement this interface, that
 * way when a thread needs to communicate with the UI it can. This is required
 * as running on top of the UI thread will degrade performance.
 * 
 * @author Kyle
 *
 */
public interface SocketResponseInterface {

	/**
	 * Message from the socket.
	 * 
	 * @param message
	 *            Message
	 */
	public void socketMessage(Message message);

	/**
	 * Error from the socket.
	 * 
	 * @param error
	 *            string
	 */
	public void socketError(String error);
}
