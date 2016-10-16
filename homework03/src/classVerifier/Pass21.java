package classVerifier;

/**
 * Class with static method call to break type data semantic checks. In this
 * case the class Pass21 is modified to not be a subclass Object.
 * 
 * @author Kyle
 */
public class Pass21 {

	/**
	 * Main Function.
	 * 
	 * @param args
	 *            not used
	 */
	public static void main(String[] args) {
		System.out.println(helloWorld(3));
	}

	/**
	 * Simple function, if x is less than 5 return true.
	 * 
	 * @param x
	 *            int input
	 * @return true if less than 5
	 */
	private static boolean helloWorld(int x) {
		if (x < 5) {
			return true;
		}
		return false;
	}
}
