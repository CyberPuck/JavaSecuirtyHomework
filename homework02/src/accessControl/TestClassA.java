package accessControl;

public class TestClassA {
	protected int variableA = 1;

	protected void setB(TestClassB b, int newValue) {
		b.variableB = newValue;
	}

	public void printA() {
		System.out.println("A: " + variableA);
	}
}
