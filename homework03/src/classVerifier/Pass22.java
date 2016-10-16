package classVerifier;

/**
 * Class with static method call to break type data semantic checks. In this
 * case the {@link classVerifier.Pass22#helloWorld(int) boolean helloWorld(int
 * x)} is changed to: boolean helloWorld().
 * 
 * @author Kyle
 */
public class Pass22 {

	/**
	 * Main function.
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

	/**
	 * Used to replace the input for the {@link Pass22.helloWorld} function
	 * using a class editor.
	 * 
	 * @return 1
	 */
	private static int test() {
		return 1;
	}
}
