package classVerifier;

/**
 * Break bytecode verification.
 * 
 * @author Kyle
 */
public class Pass31 {
	public static void main(String[] args) {
		int x;
		int y = 5;
		y++;
		x = 5;
		System.out.println(y + ", " + x);
	}
}
