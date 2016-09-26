package accessControl;

public class TestClassB {
	protected int variableB = 2;

	protected void setA(TestClassA a, int newValue) {
		a.variableA = newValue;
	}

	public void printB() {
		System.out.println("B: " + variableB);
	}
}
