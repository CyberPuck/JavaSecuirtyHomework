package quantumKeySimulator;

import java.util.Arrays;

public class QuantumKeySimulator {

	public static void main(String[] args) {
		int key_length = 9;
		// Simple simulation
		QuantumKey aliceKey = new QuantumKey(key_length);
		QuantumKey bobKey = new QuantumKey(key_length);
		// Alice generates a key
		aliceKey.generateKey();
		System.out.println(aliceKey.toString());
		// Bob generates a key
		bobKey.measureKey(aliceKey.getPhotonPolarization());
		System.out.println(bobKey.toString());
		boolean[] compare = aliceKey.compareBasis(bobKey.getBasisSet());
		System.out.println(Arrays.toString(compare));

		// simulating Eve trying to listen
		System.out.println("\nEVE IS HERE!!!!!! :(");
		QuantumKey eveKey = new QuantumKey(key_length);
		aliceKey.generateKey();
		System.out.println(aliceKey.toString());
		// Eve generates a key
		eveKey.measureKey(aliceKey.getPhotonPolarization());
		System.out.println(eveKey.toString());
		// Bob generates a key
		bobKey.measureKey(eveKey.getPhotonPolarization());
		System.out.println(bobKey.toString());
		compare = aliceKey.compareBasis(bobKey.getBasisSet());
		System.out.println(Arrays.toString(compare));
	}

}
