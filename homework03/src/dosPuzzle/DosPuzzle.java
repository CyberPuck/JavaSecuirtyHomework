package dosPuzzle;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Creates a DOS Puzzle with a defined number of puzzle bits (x + y) and number
 * of y bits; contains the x, y, and z SHA-1 hash bits.
 * 
 * @author Kyle
 *
 */
public class DosPuzzle {
	// RNG using secure random with default seed
	private SecureRandom rng = new SecureRandom();
	// number of defined bits
	private int preImageBitLength;
	// number of y bits
	private int yBitsLength;
	// represents the random x bits
	private String xBits;
	// represents the yBits
	private String yBits;
	// result from the SHA-1 HASH
	private byte[] hash;

	/**
	 * Given the total preimage size and number of y bits, create a random
	 * binary string composed of x and y bits and generate a SHA-1 hash. The x
	 * bits and hash will be exposed so the puzzle can be solved.
	 * 
	 * @param numberOfBits
	 *            total preimage puzzle size
	 * @param yBits
	 *            number of bits in the preimage to be solved for
	 */
	public DosPuzzle(int numberOfBits, int yBits) {
		// verify the puzzle is correct
		if (numberOfBits < yBits) {
			xBits = "";
			return;
		}
		// store the desired x and y sizes
		this.preImageBitLength = numberOfBits;
		this.yBitsLength = yBits;
		// calculate the bits for x and y
		// NOTE using a string representing the binary data
		String preImage = "";
		for (int i = 0; i < preImageBitLength; i++) {
			preImage = preImage + (rng.nextBoolean() ? '1' : '0');
		}
		// store the x bits
		this.xBits = preImage.substring(0, preImage.length() - this.yBitsLength);
		this.yBits = preImage.substring(preImage.length() - this.yBitsLength);
		// convert the preImage string to bytes
		byte[] xAndYBytes = new BigInteger(preImage, 2).toByteArray();

		// setup message digest as SHA-1
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-1");
			// compute the hash
			this.hash = md.digest(xAndYBytes);
		} catch (NoSuchAlgorithmException e) {
			System.err.println("SHA-1 algorithm not found!  Cannot create puzzle.");
		}
	}

	/**
	 * Gets an array of bytes representing the x bits in the puzzle.
	 * 
	 * @return Returns the byte array, note last element in array will be bit
	 *         shifted left if bits don't match byte boundary
	 */
	public String getXBits() {
		return xBits;
	}

	/**
	 * Gets the length of the y bits in the puzzle. Note the y bits themselves
	 * are not exposed as that would make solving the puzzle trivial.
	 * 
	 * @return size of y bits in number of bits
	 */
	public int getYBitLength() {
		return yBitsLength;
	}

	/**
	 * Gets the total size in bits of the pre-image (x+y).
	 * 
	 * @return pre-image size in bits.
	 */
	public int getPreImageLength() {
		return this.preImageBitLength;
	}

	/**
	 * Gets the SHA-1 generated HASH.
	 * 
	 * @return byte array representing the SHA-1 hash.
	 */
	public byte[] getZBits() {
		return this.hash;
	}
}
