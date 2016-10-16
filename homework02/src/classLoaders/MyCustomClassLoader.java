package classLoaders;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Custom implementation of a class loader extending the {@link URLClassLoader}
 * 
 * @author Kyle
 *
 */
public class MyCustomClassLoader extends URLClassLoader {

	/**
	 * Constructor, operates the same as a URLClassLoader, for this program urls
	 * can be empty.
	 * 
	 * @param urls
	 */
	public MyCustomClassLoader(URL[] urls) {
		super(urls);
	}

	/**
	 * Modifed to look for a class on a custom class path defined by the user.
	 * 
	 * @param name
	 *            is the file path to the class file (including the class file
	 *            itself).
	 */
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		// TODO Auto-generated method stub
		byte[] b = loadMyClassData(name);
		return (b == null) ? null : defineClass("TestClass", b, 0, b.length);
	}

	/**
	 * Given the string which represents the file location, read the file as
	 * binary data for the class loader to define the class.
	 * 
	 * @param name
	 *            of class to load
	 * @return byte array of read in class or NULL if failed
	 */
	private byte[] loadMyClassData(String name) {
		if (!name.isEmpty()) {
			// try to read in the file
			Path path = Paths.get(name);
			try {
				return Files.readAllBytes(path);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.err.println("Failed to read class file");
			}
		}
		// default returning null
		return null;
	}
}
