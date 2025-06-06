
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
@Constraint(validatedBy = PromotionCodeValidator.class)
public @interface ValidPromotionCode {

	String message() default "Last two digits of Promotion Code must correspond to the current year.";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};

}
