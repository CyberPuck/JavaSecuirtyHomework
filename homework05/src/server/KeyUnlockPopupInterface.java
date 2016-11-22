package server;

/**
 * Interface for exposing the password to unlock a key in a trust store.
 * 
 * @author Kyle
 */
public interface KeyUnlockPopupInterface {
	/**
	 * Function for returning the given password in a pop up dialog.
	 * 
	 * @param password
	 *            Used to unlock a key in a trust store.
	 */
	public void unlockKey(char[] password);
}
