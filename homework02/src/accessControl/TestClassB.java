package accessControl;

/**
 * Class with a protected variable.
 * 
 * @author Kyle
 *
 */
public class TestClassB {
	protected int variableB = 2;

	protected void setA(TestClassA a, int newValue) {
		a.variableA = newValue;
	}

	/**
	 * Prints the integer variable variableB.
	 */
	public void printB() {
		System.out.println("B: " + variableB);
	}
}
