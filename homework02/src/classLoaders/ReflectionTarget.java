package classLoaders;

/**
 * Reflection target in a different package.
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
