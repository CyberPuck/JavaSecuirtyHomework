package classVerifier;

/**
 * Class with static method call to break type data semantic checks.
 * 
 * @author Kyle
 */
public class Pass22 {
	public static void main(String[] args) {
		System.out.println(helloWorld(3));
	}

	private static boolean helloWorld(int x) {
		if (x < 5) {
			return true;
		}
		return false;
	}
}
