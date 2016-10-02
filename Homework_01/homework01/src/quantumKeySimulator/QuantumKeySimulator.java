package quantumKeySimulator;

public class QuantumKeySimulator {

	public static void main(String[] args) {
		int key_length = 20;
		// Simple simulation
		QuantumKey aliceKey = new QuantumKey(key_length);
		QuantumKey bobKey = new QuantumKey(key_length);
		// Alice generates a key
		aliceKey.generateKey();
		System.out.println("---Alice---");
		System.out.println(aliceKey.toString());
		// Bob generates a key
		bobKey.measureKey(aliceKey.getPhotonPolarization());
		System.out.println("---Bob---");
		System.out.println(bobKey.toString());
		System.out.println(aliceKey.compareBasis(bobKey.getBasisSet()));

		// simulating Eve trying to listen
		System.out.println("---Alice---");
		QuantumKey eveKey = new QuantumKey(key_length);
		aliceKey.generateKey();
		System.out.println(aliceKey.toString());
		// Eve generates a key
		eveKey.measureKey(aliceKey.getPhotonPolarization());
		System.out.println("---Eve---");
		System.out.println(eveKey.toString());
		// Bob generates a key
		bobKey.measureKey(eveKey.getPhotonPolarization());
		System.out.println("---Bob---");
		System.out.println(bobKey.toString());
		System.out.println(bobKey.compareBasis(aliceKey.getBasisSet()));
		System.out.println(aliceKey.compareKey(bobKey.getKey(), bobKey.getBasisSet()));
	}

}
