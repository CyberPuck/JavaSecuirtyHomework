package accessControl;

import java.lang.reflect.Field;

/**
 * Class for testing java reflections.
 * 
 * @author Kyle
 *
 */
public class ReflectionTester {
	/**
	 * Internal class with a private integer x. Reflection will be used to
	 * modify the value.
	 * 
	 * @author Kyle
	 */
	class X {
		private int x = 1;

		/**
		 * Prints x.
		 */
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
	 *            test class, must be of type X.
	 */
	public ReflectionTester(Object newObject) {
		x = newObject;
	}

	/**
	 * Gets the value of the internal x variable.
	 * 
	 * @return String representing x.
	 */
	public String getX() {
		return x.toString();
	}

	/**
	 * Using reflection, modifies X.x with a new integer value.
	 * 
	 * @param newValue
	 *            integer to update x.
	 * @throws Exception
	 *             thrown if an error with the reflection occurs.
	 */
	public void ModifyX(int newValue) throws Exception {
		Field f = x.getClass().getDeclaredField("x");
		// enable modification of the value
		f.setAccessible(true);
		f.setInt(x, newValue);
	}
}
