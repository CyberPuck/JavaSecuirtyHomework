package client;

/**
 * Handles information being passed from the server login pop up to the client
 * controller.
 * 
 * @author Kyle
 */
public interface ServerLoginPopupInterface {
	/**
	 * Handles a login request from the server login popup.
	 * 
	 * @param attr
	 *            Attributes entered by the user to access the server.
	 */
	public void login(ServerAttributes attr);
}
