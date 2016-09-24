package classLoaders;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MyCustomClassLoader extends URLClassLoader {

	public MyCustomClassLoader(URL[] urls) {
		super(urls);
		System.out.println("Let URLClassLoader handle construction");
	}
	
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		// TODO Auto-generated method stub
		byte[] b = loadMyClassData(name);
		return (b == null) ? null : defineClass(name, b, 0, b.length);
	}

	/**
	 * Given the string which represents the file location, read the file as binary
	 * data for the class loader to define the class.
	 * @param name of class to load
	 * @return byte array of read in class or NULL if failed
	 */
	private byte[] loadMyClassData(String name) {
		if(!name.isEmpty()) {
			// try to read in the file
			Path path = Paths.get(name);
			Path abs = path.toAbsolutePath();
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
