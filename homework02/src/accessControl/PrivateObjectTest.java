package accessControl;

/**
 * Main class for testing private members, contains two Temp classes with
 * private variables.
 * 
 * @author Kyle
 */
public class PrivateObjectTest {
	/**
	 * Class for testing private member read/write access.
	 * 
	 * @author Kyle
	 */
	class Temp {
		private int x;

		/**
		 * Print variable x.
		 */
		public void printX() {
			System.out.println(x);
		}

		/**
		 * Get the value of private variable x.
		 * 
		 * @return int - variable x
		 */
		public int getX() {
			return x;
		}

		/**
		 * Set the value of x.
		 * 
		 * @param x
		 *            new integer value of x.
		 */
		public void setX(int x) {
			this.x = x;
		}

		/**
		 * Given a temp class and a new integer value, this method will update
		 * the temp class's private variable.
		 * 
		 * @param t
		 *            Temp class with variable x.
		 * @param newValue
		 *            new integer value for x.
		 */
		public void setTemp(Temp t, int newValue) {
			t.setX(newValue);
		}
	}

	/**
	 * Temp class with private variable x.
	 */
	public Temp temp1 = new Temp();
	/**
	 * Temp class with private variable x.
	 */
	public Temp temp2 = new Temp();

	/**
	 * Print the private variables in the two Temp classes.
	 */
	public void printPrivateClasses() {
		System.out.print("Temp 1: ");
		temp1.printX();
		System.out.print("Temp 2: ");
		temp2.printX();
	}
}
