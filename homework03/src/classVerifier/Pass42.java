package classVerifier;

/**
 * Breaks external references. In this case the call to an external class will
 * fail as that class has been set to null.
 * 
 * @author Kyle
 *
 */
public class Pass42 {

	/**
	 * Main function.
	 * 
	 * @param args
	 *            not used
	 */
	public static void main(String[] args) {
		// called to verify the invalid class file was loaded and running
		System.out.println("Main started...");
		Pass42TestClass init = new Pass42TestClass();
		// create the test class
		Pass42TestClass test = init; // init will be replaced with NULL
		System.out.println(test.getSecret());
	}

}
