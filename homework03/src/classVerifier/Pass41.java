package classVerifier;

/**
 * Breaks External References. In this case integerOne will be set to null so a
 * runtime exception is hit.
 * 
 * @author Kyle
 *
 */
public class Pass41 {

	/**
	 * Main function.
	 * 
	 * @param args
	 *            not used
	 */
	public static void main(String[] args) {
		// called to verify the invalid class file was loaded and running
		System.out.println("Main started...");
		Integer integerOne = new Integer(1);
		Integer integerTwo = new Integer(2);
		Integer nullInt = null;
		String stringOne = "Hello World";
		// Will be modified in class file
		integerOne = (Integer) integerTwo;
		// print out variables
		System.out.println("1=" + integerOne.toString() + "\n2=" + integerTwo.toString() + "\nStr=" + stringOne);
	}
}
