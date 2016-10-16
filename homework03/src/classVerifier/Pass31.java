package classVerifier;

/**
 * Break bytecode verification. In this case y++ is modified to x++ in the class
 * file, this fails as x has not been initialized yet.
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
