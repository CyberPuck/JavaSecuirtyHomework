package utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * File input/output utilities for homework01.
 * 
 * @author Cyber_Puck
 */
public class FileIO {

	/**
	 * Reads in a file and returns the string.
	 * 
	 * @param file
	 * @return String representing the file contents
	 * @throws IOException
	 *             If an error occurs during reading an error is thrown
	 */
	public static String readFile(File file) throws IOException {
		// try-with-resources, no need to close out the fle
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			StringBuilder stringBuilder = new StringBuilder();
			String line = br.readLine();
			// added to prevent adding invalid separators
			stringBuilder.append(line);
			line = br.readLine();

			while (line != null) {
				stringBuilder.append(System.lineSeparator());
				stringBuilder.append(line);
				line = br.readLine();
			}
			return stringBuilder.toString();
		}
	}

	/**
	 * Writes a file with the provided String fileContents.
	 * 
	 * @param file
	 * @param fileContents
	 *            data to write to file
	 * @throws IOException
	 *             If an error occurred during writing an exception is thrown
	 */
	public static void writeFile(File file, String fileContents)
			throws IOException {
		// try-with-resources, no need to close out the fle
		try (BufferedWriter wr = new BufferedWriter(new FileWriter(file))) {
			wr.write(fileContents);
		}
	}
}
