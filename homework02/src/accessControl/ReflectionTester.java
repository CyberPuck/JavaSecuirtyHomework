package accessControl;

import java.lang.reflect.Field;

public class ReflectionTester {
	class X {
		private int x = 1;

		@Override
		public String toString() {
			return "X: " + x;
		}
	}

	private Object x;

	/**
	 * Constructor for local X class
	 */
	public ReflectionTester() {
		x = new X();
	}

	/**
	 * Constructor for included test class
	 * 
	 * @param newObject
	 *            test class
	 */
	public ReflectionTester(Object newObject) {
		x = newObject;
	}

	public String getX() {
		return x.toString();
	}

	public void ModifyX(int newValue) throws Exception {
		Field f = x.getClass().getDeclaredField("x");
		// enable modification of the value
		f.setAccessible(true);
		f.setInt(x, newValue);
	}
}
