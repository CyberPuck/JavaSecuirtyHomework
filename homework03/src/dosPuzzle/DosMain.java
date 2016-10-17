package dosPuzzle;

/**
 * Entry point for the DOS Puzzle exercise.
 * 
 * @author Kyle
 *
 */
public class DosMain {

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			DosPuzzle puzzle1 = new DosPuzzle(50, 1);
			System.out.println(puzzle1.getXBits());
			System.out.println(puzzle1.getYBits());
			System.out.println(puzzle1.getZBits());
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

}
