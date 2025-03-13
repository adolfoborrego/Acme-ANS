/*
 * ValidEmployeeId.java
 *
 * Custom annotation to validate employee identifiers.
 */

package acme.constraints;

/*
 * ValidEmployeeId.java
 *
 * Custom annotation to validate employee identifiers.
 */

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmployeeCodeInitialsValidator.class)
public @interface ValidEmployeeCodeInitials {

	String message() default "{identifier}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
