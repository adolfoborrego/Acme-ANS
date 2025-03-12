
package acme.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = EmployeeCodeValidator.class)
public @interface ValidEmployeeCode {

	String message() default "El EmployeeCode debe ser único para Managers, Agents y Members.";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};

}
