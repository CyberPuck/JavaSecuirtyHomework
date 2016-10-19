package dosPuzzle;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Designed to solve the DOS puzzle, it uses a brute force search method. Start
 * with 0x00 then going up until the SHA-1 hash matches the given puzzle hash.
 * 
 * @author Kyle
 *
 */
public class DosPuzzleSolver {
	// Local copy of the puzzle
	private DosPuzzle puzzle;

	/**
	 * Stores the DOS puzzle. Run the solve method to solve.
	 * 
	 * @param puzzle
	 *            DOS Puzzle to solve.
	 */
	public DosPuzzleSolver(DosPuzzle puzzle) {
		this.puzzle = puzzle;
	}

	public long solveDosPuzzle() {
		// get start time
		long startTime = System.currentTimeMillis();
		try {
			// setup the message digest a head of time
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			int totalSize = (int) Math.pow(2, puzzle.getYBitLength());
			for (int i = 0; i < totalSize; i++) {
				// create a test input string
				String testImage = puzzle.getXBits() + Integer.toBinaryString(i);
				// convert test string to bytes
				byte[] testInput = new BigInteger(testImage, 2).toByteArray();
				// if the hashes match break out, solution has been found
				if (Arrays.equals(puzzle.getZBits(), md.digest(testInput))) {
					break;
				}
			}
		} catch (NoSuchAlgorithmException e) {
			System.err.println("Solve failed, SHA-1 algorithm does not exist");
		}
		// get finish time
		long endTime = System.currentTimeMillis();
		// calculate time to solve
		return endTime - startTime;
	}
}
