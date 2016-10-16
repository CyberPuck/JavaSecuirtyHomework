package accessControl;

/**
 * Class with a protected variable.
 * 
 * @author Kyle
 *
 */
public class TestClassA {
	protected int variableA = 1;

	protected void setB(TestClassB b, int newValue) {
		b.variableB = newValue;
	}

	/**
	 * Prints the integer variable variableA.
	 */
	public void printA() {
		System.out.println("A: " + variableA);
	}
}
