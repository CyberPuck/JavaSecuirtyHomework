package classVerifier;

/**
 * Class with static method call to break type data semantic checks. In this
 * case the class Pass21 is modified to not be a subclass Object.
 * 
 * @author Kyle
 */
public class Pass21 {
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
