package dosPuzzle;

/**
 * Entry point for the DOS Puzzle exercise. Will run a number of DOS puzzles to
 * get stats on time to complete.
 * 
 * @author Kyle
 *
 */
public class DosMain {

	/**
	 * Main Function, runs the tests to get mean, variance, and standard
	 * deviation to solve DOS puzzle.
	 * 
	 * @param args
	 *            not used
	 */
	public static void main(String[] args) {
		// print out the header
		System.out.println("---All times measured in milliseconds---");
		System.out.println("Y bits\t\tAvg\t\tVar\t\tSD");
		int numberOfBits = 60;
		int maxYBits = 18;
		int numberOfRuns = 50;
		for (int numYBits = 1; numYBits <= maxYBits; numYBits++) {
			long[] timesToComplete = new long[numberOfRuns];
			for (int i = 0; i < numberOfRuns; i++) {
				DosPuzzle puzzle = new DosPuzzle(numberOfBits, numYBits);
				DosPuzzleSolver solver = new DosPuzzleSolver(puzzle);
				timesToComplete[i] = solver.solveDosPuzzle();
			}
			System.out.println(
					String.format("%02d", numYBits) + "\t\t" + String.format("%.2f", calculateMean(timesToComplete))
							+ "\t\t" + String.format("%.2f", calculateVariance(timesToComplete)) + "\t\t"
							+ String.format("%.2f", calculateStandardDeviation(timesToComplete)));
		}
	}

	private static double calculateMean(long[] data) {
		double mean = 0.0;
		for (long point : data) {
			mean += point;
		}
		return mean / data.length;
	}

	private static double calculateVariance(long[] data) {
		double mean = calculateMean(data);
		double variance = 0.0;
		for (long point : data) {
			variance += (point - mean) * (point - mean);
		}
		return variance / data.length;
	}

	private static double calculateStandardDeviation(long[] data) {
		return Math.sqrt(calculateVariance(data));
	}
}
