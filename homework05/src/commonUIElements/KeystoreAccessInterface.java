package commonUIElements;

/**
 * Interface allowing the client and server to use the popup controller.
 * 
 * @author Kyle
 *
 */
public interface KeystoreAccessInterface {
	/**
	 * Function allowing the password to the key store and trust store to be
	 * passed to the primary controller class.
	 * 
	 * @param keyStorePassword
	 *            to the key store
	 * @param trustStorePassword
	 *            to the trust store
	 */
	public void onLoginRequest(char[] keyStorePassword, char[] trustStorePassword);
}
