package classVerifier;

/**
 * Break bytecode verification, bytecode will be malformed. The bytecode for
 * line 13 (y++) is changed to aconst_null.
 * 
 * @author Kyle
 */
public class Pass32 {
	public static void main(String[] args) {
		int x;
		int y = 5;
		y++;
		x = 5;
		System.out.println(y + ", " + x);
	}
}
