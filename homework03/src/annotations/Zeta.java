package annotations;

/**
 * Class handling security annotations. The test methods m1, m2, m3 are included
 * as well as the defined annotations for each method.
 * 
 * @author Kyle
 *
 */
public class Zeta {

	/**
	 * Annotation for an authorization check.
	 * 
	 * @author Kyle
	 *
	 */
	public @interface authorizationCheck {
	}

	/**
	 * Annotation of locking critical resources.
	 * 
	 * @author Kyle
	 *
	 */
	public @interface lockCriticalResource {
	}

	/**
	 * Annotation for auditing an event.
	 * 
	 * @author Kyle
	 *
	 */
	public @interface auditEvent {
	}

	/**
	 * This function needs to check authorization and audit the event. Could be
	 * used for a login attempt.
	 */
	@authorizationCheck
	@auditEvent
	public void m1() {
	}

	/**
	 * This function needs to lock a critical resource. Could be opening a file.
	 */
	@lockCriticalResource
	public void m2() {

	}

	/**
	 * This function needs an authorization check, lock a critical resource, and
	 * audit the event. This could be an admin attempting to modify a user role
	 * or attribute.
	 */
	@authorizationCheck
	@lockCriticalResource
	@auditEvent
	public void m3() {

	}
}
