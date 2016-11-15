package commonUIElements;

/**
 * Interface allowing the client and server to use the popup controller.
 * 
 * @author Kyle
 *
 */
public interface KeystoreAccessInterface {
	/**
	 * Function allowing the password to the keystore to be passed to the
	 * primary controller class.
	 * 
	 * @param password
	 *            to the keystore
	 */
	public void onLoginRequest(char[] password);
}
