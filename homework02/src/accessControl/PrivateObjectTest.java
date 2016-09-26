package accessControl;

/**
 * Part 2 test.
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

		public void printX() {
			System.out.println(x);
		}

		public int getX() {
			return x;
		}

		public void setX(int x) {
			this.x = x;
		}
		
		public void setTemp(Temp t, int newValue) {
			t.setX(newValue);
		}
	}

	// classes with private members
	public Temp temp1 = new Temp();
	public Temp temp2 = new Temp();

	public void printPrivateClasses() {
		System.out.print("Temp 1: ");
		temp1.printX();
		System.out.print("Temp 2: ");
		temp2.printX();
	}
}
