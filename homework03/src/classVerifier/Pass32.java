package classVerifier;

/**
 * Break bytecode verification, bytecode will be malformed.
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
