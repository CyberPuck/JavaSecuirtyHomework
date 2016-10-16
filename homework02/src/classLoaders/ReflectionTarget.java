package classLoaders;

/**
 * Reflection target in the {@link accessControl.AccessControlTester} class.
 * 
 * @author Kyle
 */
public class ReflectionTarget {
	protected int x = 1;

	@Override
	public String toString() {
		return "Protected X: " + x;
	}
}
