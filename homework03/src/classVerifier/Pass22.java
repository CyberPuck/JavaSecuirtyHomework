package classVerifier;

/**
 * Class with static method call to break type data semantic checks. In this
 * case the {@link classVerifier.Pass22#helloWorld(int) boolean helloWorld(int
 * x)} is changed to: boolean helloWorld().
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

	private static int test() {
		return 1;
	}
}
