public class TestClass {

	public boolean equals(Object o) {
		if (o instanceof TestClass)
			System.out.println("yes, it's one of us!");
		else
			System.out.println("no, it's not one of us!");
		return this == (TestClass)o;
	}
}
