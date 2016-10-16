package classVerifier;

/**
 * Break bytecode verification, bytecode will be malformed. The bytecode for
 * y++; (iinc) is changed to aconst_null.
 * 
 * @author Kyle
 */
public class Pass32 {

	/**
	 * Main function.
	 * 
	 * @param args
	 *            not used
	 */
	public static void main(String[] args) {
		int x;
		int y = 5;
		y++; // bytecode will be updated
		x = 5;
		System.out.println(y + ", " + x);
	}
}
