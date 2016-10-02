package accessControl;

import classLoaders.ReflectionTarget;

/**
 * Tests HW02 Part 2.
 * 
 * @author Kyle
 */
public class AccessControlTester {

	public static void main(String[] args) {
		System.out.println("---Test Case 1---");
		TestClassA a = new TestClassA();
		TestClassB b = new TestClassB();
		a.printA();
		b.printB();
		System.out.println("A attempting to modify B and B doing the same");
		a.setB(b, 55);
		b.setA(a, -100);
		a.printA();
		b.printB();

		System.out.println("---Test Case 2---");
		PrivateObjectTest tester2 = new PrivateObjectTest();
		tester2.printPrivateClasses();
		// This fails, as expected
		// tester2.temp1.x = 5;
		// instead someone added getters and setters to the class, exposing the
		// private variable
		tester2.temp1.setTemp(tester2.temp2, 5);
		tester2.temp2.setTemp(tester2.temp1, -5);
		tester2.printPrivateClasses();

		System.out.println("---Test Case 3---");
		try {
			System.out.println("Local class private fields");
			ReflectionTester reflector = new ReflectionTester();
			System.out.println("Before: " + reflector.getX());
			reflector.ModifyX(5280);
			System.out.println("After: " + reflector.getX());
			System.out.println("Testing out of package protected fields");
			ReflectionTarget protectedTarget = new ReflectionTarget();
			// x is not visible to this class
			// protectedTarget.x = 5;
			ReflectionTester protectedReflector = new ReflectionTester(protectedTarget);
			System.out.println("Before: " + protectedReflector.getX());
			protectedReflector.ModifyX(-9001);
			System.out.println("After: " + protectedReflector.getX());
		} catch (Exception e) {
			System.err.println("Part 3 exception: " + e.getMessage());
		}
	}
}
