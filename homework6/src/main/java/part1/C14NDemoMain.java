package part1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.xml.sax.SAXException;

/**
 * Handles demonstrating the Apache C14N implementation.
 * 
 * @author Kyle
 */
public class C14NDemoMain {

	/**
	 * Entry point for HW6, part 1.
	 * 
	 * @param args
	 *            list of files to run through canonical process.
	 */
	public static void main(String[] args) {
		// NOTE: JVM told me to
		org.apache.xml.security.Init.init();
		
		
		
		// setup the C14N canonicalizer
		try {
			Canonicalizer canon = Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N11_OMIT_COMMENTS);
			for(String path : args) {
				// try reading in the file and running the canonicalizer
				try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path))))) {
					byte[] data = Files.readAllBytes(Paths.get(path));
					String uncanonicalized = new String(data);
					byte[] result = canon.canonicalize(data);
					String plainText = new String(result);
					System.out.println(uncanonicalized);
					System.out.println("+++++++++++++++++++++++++++++++");
					System.out.println("    C14N Applied");
					System.out.println("+++++++++++++++++++++++++++++++");
					System.out.println(plainText);
				} catch(FileNotFoundException e) {
					System.err.println("Failed to read file: " + path + " because: " + e.getMessage());
				} catch(IOException e) {
					System.err.println("Failed to read in file: " + path);
				} catch(CanonicalizationException |SAXException | ParserConfigurationException e) {
					System.err.println("Failed to canonicalize " + path + " " + e.getMessage());
				}
			}
		} catch (InvalidCanonicalizerException e) {
			System.err.println("Failed to get canonicalizer C14N: " + e.getMessage());
		}
	}

}
