package classLoaders;

import java.net.URL;

public class ClassLoaderMain {

	public static void main(String[] args) {
		// printing PWD
		// TODO: Remove
//		System.out.println("Working Directory = " +
//	              System.getProperty("user.dir"));
		// feeding in the relative location of the test class
		String classLocation = "../DMZ/TestClass.class";
		URL[] urls = {};
		MyCustomClassLoader loader = new MyCustomClassLoader(urls);
		try {
			Class test = loader.findClass(classLocation);
			System.out.println(test.hashCode());
		} catch(ClassNotFoundException e) {
			System.err.println("Loader failed to load class");
		}
	}

}
