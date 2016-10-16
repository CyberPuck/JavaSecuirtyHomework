package classVerifier;

/**
 * Break bytecode verification. In this case y++ is modified to x++ in the class
 * file, this fails as x has not been initialized yet.
 * 
 * @author Kyle
 */
public class Pass31 {

	/**
	 * Main Function.
	 * 
	 * @param args
	 *            not used
	 */
	public static void main(String[] args) {
		int x;
		int y = 5;
		y++; // Will be updated in class editor
		x = 5;
		System.out.println(y + ", " + x);
	}
}
