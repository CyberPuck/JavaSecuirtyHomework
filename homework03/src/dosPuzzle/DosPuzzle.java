package dosPuzzle;

import java.security.MessageDigest;
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
	// represents the random x+ y bits array
	private byte[] xAndYBytes;
	// result from the SHA-1 HASH
	private byte[] hash;

	public DosPuzzle(int numberOfBits, int yBits) throws Exception {
		// verify the puzzle is correct
		if (numberOfBits < yBits) {
			throw new Exception("Number of Y bits cannot exceed the total number of bits in the puzzle.");
		}
		// store the desired x and y sizes
		this.preImageBitLength = numberOfBits;
		this.yBitsLength = yBits;
		// calculate the bits for x and y
		// since bytes are 8 bits, calculate the number of bytes required for x
		// + y
		int numberOfBytes = (int) Math.ceil(this.preImageBitLength / 8.0);
		this.xAndYBytes = new byte[numberOfBytes];
		rng.nextBytes(this.xAndYBytes);
		// clear out extra bits at the end
		int extraBits = this.preImageBitLength % 8;
		xAndYBytes[xAndYBytes.length - 1] = (byte) (xAndYBytes[xAndYBytes.length - 1] >> extraBits);

		// setup message digest as SHA-1
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		// compute the hash
		this.hash = md.digest(xAndYBytes);
	}

	/**
	 * Gets an array of bytes representing the x bits in the puzzle.
	 * 
	 * @return Returns the byte array, note last element in array will be bit
	 *         shifted left if bits don't match byte boundary
	 */
	public byte[] getXBits() {
		int numXBytes = (int)Math.floor((this.preImageBitLength - this.yBitsLength) / 8.0);
		// check if the x bits match the byte boundary
		if((this.preImageBitLength - this.yBitsLength) % 8 != 0) {
			numXBytes++;
		}
		byte[] xBytes = new byte[numXBytes];
		xBytes[xBytes.length - 1] = (byte) (xBytes[xBytes.length - 1] >> ((this.preImageBitLength - this.yBitsLength) % 8));
		return xBytes;
	}

	/**
	 * TODO: Remove, for testing only.
	 * 
	 * @return byte array representing y.
	 */
	public byte[] getYBits() {
		int numYBytes = (int)Math.floor((this.yBitsLength) / 8.0);
		// check if the y bits match the byte boundary
		if((this.yBitsLength) % 8 != 0) {
			numYBytes++;
		}
		byte[] yBytes = new byte[numYBytes];
		yBytes[yBytes.length - 1] = (byte) (yBytes[yBytes.length - 1] >> ((this.yBitsLength) % 8));
		return yBytes;
	}

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
