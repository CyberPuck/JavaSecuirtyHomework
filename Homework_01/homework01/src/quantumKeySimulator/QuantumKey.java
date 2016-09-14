package quantumKeySimulator;

import java.security.SecureRandom;

/**
 * Represents a quantum key. This includes the bits and filters used.
 * 
 * @author Cyber_Puck
 */
public class QuantumKey {
	private SecureRandom rng;
	// using booleans to represent binary numbers
	private boolean[] key;
	
	public QuantumKey(int keyLength) {
		// setup the key
		key = new boolean[keyLength];
		rng = new SecureRandom();
		// generate the key
		for(int i = 0; i < keyLength; i++) {
			
		}
	}
}
