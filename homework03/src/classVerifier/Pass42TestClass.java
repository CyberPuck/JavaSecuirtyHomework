package classVerifier;

/**
 * Simple class with a private string that is a secret. Used in Pass42.
 * 
 * @author Kyle
 *
 */
public class Pass42TestClass {
	// Super secret string!
	private String superSecret = "It's a secret";

	/**
	 * Accessor for getting the class secret.
	 * 
	 * @return String the secret
	 */
	public String getSecret() {
		return superSecret;
	}
}
