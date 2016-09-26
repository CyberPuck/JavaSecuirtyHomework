package classLoaders;

import java.io.IOException;
import java.net.URL;

/**
 * Tests HW02 Part 1.
 * 
 * @author Kyle
 */
public class ClassLoaderMain {

	public static void main(String[] args) {
		// feeding in the relative location of the test class
		String classLocation = "../DMZ/TestClass.class";
		URL[] urls = {};
		// try with resources
		try (MyCustomClassLoader loader = new MyCustomClassLoader(urls)) {
			// part 1: 1 loader, two loaded classes
			System.out.println("---One class loader, two loaded classes---");
			Class test = loader.loadClass(classLocation);
			Object object1 = test.newInstance();
			Object object2 = test.newInstance();
			System.out
					.println("TestClass: " + test.hashCode() + " loaded by MyCustomeClassLoader: " + loader.hashCode());
			System.out
					.println("TestClass: " + test.hashCode() + " loaded by MyCustomeClassLoader: " + loader.hashCode());
			System.out.println(
					"TestClass: " + object1.hashCode() + " loaded by MyCustomeClassLoader: " + loader.hashCode());
			System.out.println(
					"TestClass: " + object2.hashCode() + " loaded by MyCustomeClassLoader: " + loader.hashCode());
			if (object2.equals(object1)) {
				System.out.println("Objects are equal");
			} else {
				System.out.println("Objects are NOT equal");
			}
		} catch (ClassNotFoundException | IllegalAccessException | InstantiationException | IOException e) {
			System.err.println("Error in part 1");
		}

		// try with resources
		try (MyCustomClassLoader loader1 = new MyCustomClassLoader(urls);
				MyCustomClassLoader loader2 = new MyCustomClassLoader(urls);) {
			// part 2: 2 loaders, two different loaded classes
			System.out.println("---Two class loaders, two loaded classes---");
			// load classes
			Class test1 = loader1.loadClass(classLocation);
			Class test2 = loader2.loadClass(classLocation);
			// get the instances
			Object object1 = test1.newInstance();
			Object object2 = test2.newInstance();
			System.out.println(
					"TestClass: " + test1.hashCode() + " loaded by MyCustomeClassLoader: " + loader1.hashCode());
			System.out.println(
					"TestClass: " + test2.hashCode() + " loaded by MyCustomeClassLoader: " + loader2.hashCode());
			System.out.println(
					"TestClass: " + object1.hashCode() + " loaded by MyCustomeClassLoader: " + loader1.hashCode());
			System.out.println(
					"TestClass: " + object2.hashCode() + " loaded by MyCustomeClassLoader: " + loader2.hashCode());
			if (object1.equals(object2)) {
				System.out.println("Objects are equal");
			} else {
				System.out.println("Objects are NOT equal");
			}
		} catch (ClassNotFoundException | IllegalAccessException | InstantiationException | IOException e) {
			System.err.println("Error in part 2");
		}
	}

}
