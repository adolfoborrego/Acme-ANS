
package acme.constraints;

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
@Constraint(validatedBy = TrackingLogValidator.class)
public @interface ValidTrackingLog {

	String message() default "TrackingLog no válido";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};

}
