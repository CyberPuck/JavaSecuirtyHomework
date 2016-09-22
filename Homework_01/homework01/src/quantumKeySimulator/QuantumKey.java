package quantumKeySimulator;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Represents a quantum key. This includes the bits and filters used.
 * 
 * @author Cyber_Puck
 */
public class QuantumKey {
	// represents the quantum basis available
	public enum QuantumBasis {
		RECTILINEAR, DIAGONAL
	};

	// represents the polarization from the measurements
	public enum Polarization {
		VERTICAL, HORIZONTAL, DIAGONAL_UP, DIAGONAL_DOWN
	};

	// RNG for generating both the key and basis
	private SecureRandom rng;
	// binary key
	private int[] key;
	// enum values represent the selected basis
	private QuantumBasis[] basisSet;
	// measurements based on the key and basis values
	private Polarization[] photonPolarization;

	public QuantumKey(int keyLength) {
		// setup the arrays
		key = new int[keyLength];
		basisSet = new QuantumBasis[keyLength];
		photonPolarization = new Polarization[keyLength];
		// start up the RNG
		try {
			rng = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
			System.err.println("Can't use SHA1PRNG");
			rng = new SecureRandom();
		}
	}

	/**
	 * Generates the quantum key and components from scratch
	 */
	public void generateKey() {
		// generate the key and basis
		for (int i = 0; i < key.length; i++) {
			key[i] = rng.nextBoolean() ? 1 : 0;
			// either rectilinear or diagonal
			basisSet[i] = QuantumBasis.values()[rng.nextBoolean() ? 1 : 0];
			// based on key value and basis calculate the polarization
			if (basisSet[i].equals(QuantumBasis.RECTILINEAR)) {
				// positions 0 and 1 correspond to the binary value of the basis
				photonPolarization[i] = Polarization.values()[key[i]];
			} else {
				// positions 2 and 3 correspond to the binary value of the basis
				photonPolarization[i] = Polarization.values()[key[i] + 2];
			}
		}
	}

	/**
	 * Given a set of polarizations guess the basis and measure the binary
	 * output.
	 * 
	 * @param sentPolarizations
	 */
	public void measureKey(Polarization[] sentPolarizations) {
		if (sentPolarizations.length != key.length) {
			System.err.println("Polarization array and key array lengths don't match");
			return;
		}

		for (int i = 0; i < key.length; i++) {
			// guess basis
			basisSet[i] = QuantumBasis.values()[rng.nextBoolean() ? 1 : 0];
			// measure the bit value
			switch (sentPolarizations[i]) {
			case VERTICAL:
				if (basisSet[i] == QuantumBasis.RECTILINEAR)
					key[i] = 0;
				else
					key[i] = 0;
				break;
			case HORIZONTAL:
				if (basisSet[i] == QuantumBasis.RECTILINEAR)
					key[i] = 1;
				else
					key[i] = 0;
				break;
			case DIAGONAL_UP:
				if (basisSet[i] == QuantumBasis.DIAGONAL)
					key[i] = 0;
				else
					key[i] = 0;
				break;
			case DIAGONAL_DOWN:
				if (basisSet[i] == QuantumBasis.DIAGONAL)
					key[i] = 1;
				else
					key[i] = 0;
				break;
			default:
				System.err.println("Unknown polarization!");
				return;
			}
			// randomly select basis, based on key select valid polarization
			// Needed for Eve
			QuantumBasis randomBasis = QuantumBasis.values()[rng.nextBoolean() ? 1 : 0];
			if (randomBasis.equals(QuantumBasis.RECTILINEAR)) {
				// positions 0 and 1 correspond to the binary value of the basis
				photonPolarization[i] = Polarization.values()[key[i]];
			} else {
				// positions 2 and 3 correspond to the binary value of the basis
				photonPolarization[i] = Polarization.values()[key[i] + 2];
			}
		}
	}

	/**
	 * Compares the key's current basis set with an input basis set. The
	 * returned array indicates which indices in the basis input array match the
	 * key's.
	 * 
	 * @param inputBasis
	 *            input basis set
	 * @return String indicating if Basis match
	 */
	public String compareBasis(QuantumBasis[] inputBasis) {
		StringBuilder builder = new StringBuilder("Compare:");
		for (int i = 0; i < basisSet.length; i++) {
			if (inputBasis[i].equals(basisSet[i]))
				builder.append(key[i] + ", ");
			else
				builder.append("-, ");
		}
		return builder.toString();
	}

	/**
	 * Compare keys to see if the bit values match. An error only occurs if the
	 * basis match but the bits do not.
	 * 
	 * @param inputKey
	 *            input binary key
	 * @return String indicating bit errors
	 */
	public String compareKey(int[] inputKey, QuantumBasis[] inputBasis) {
		StringBuilder builder = new StringBuilder("Errors: ");
		for (int i = 0; i < key.length; i++) {
			if (inputBasis[i].equals(basisSet[i])) {
				if (inputKey[i] != key[i]) {
					builder.append("E, "); // Eve error
				} else {
					builder.append("-, ");
				}
			} else {
				builder.append("-, "); // not considered
			}
		}
		return builder.toString();
	}

	/**
	 * Gets the binary key.
	 * 
	 * @return int array representing the binary key
	 */
	public int[] getKey() {
		return key;
	}

	/**
	 * Gets the measured polarization of the key.
	 * 
	 * @return Polarization of the generated key
	 */
	public Polarization[] getPhotonPolarization() {
		return photonPolarization;
	}

	/**
	 * Gets the generated basis set of the key.
	 * 
	 * @return Basis set of the generated key
	 */
	public QuantumBasis[] getBasisSet() {
		return basisSet;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder("QuantumKey:\n");
		// add in the key
		stringBuilder.append("Key:    ");
		for (int i : key) {
			if (i < 0) {
				stringBuilder.append("-, ");
				continue;
			}
			stringBuilder.append(i + ", ");
		}
		// add in basis polarization
		stringBuilder.append("\nBasis:  ");
		for (QuantumBasis basis : this.basisSet) {
			if (basis.equals(QuantumBasis.RECTILINEAR)) {
				stringBuilder.append("+, ");
			} else {
				stringBuilder.append("X, ");
			}
		}
		// add in polarization filters
		stringBuilder.append("\nPhotons:");
		for (Polarization polarization : this.photonPolarization) {
			if (polarization == null) {
				stringBuilder.append("., ");
			} else if (polarization.equals(Polarization.VERTICAL)) {
				stringBuilder.append("|, ");
			} else if (polarization.equals(Polarization.HORIZONTAL)) {
				stringBuilder.append("-, ");
			} else if (polarization.equals(Polarization.DIAGONAL_UP)) {
				stringBuilder.append("/, ");
			} else {
				stringBuilder.append("\\, ");
			}
		}
		return stringBuilder.toString();
		// return "QuantumKey [key=" + Arrays.toString(key) + ",\nbasisSet="
		// + Arrays.toString(basisSet) + ",\nphotonPolarization="
		// + Arrays.toString(photonPolarization) + "]";
	}
}
