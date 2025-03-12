
package acme.constraints;

import java.time.Year;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.validation.AbstractValidator;

public class PromotionCodeValidator extends AbstractValidator<ValidPromotionCode, String> {

	@Override
	public boolean isValid(final String value, final ConstraintValidatorContext context) {
		if (value == null || value.trim().isEmpty())
			return false;

		String currentYearSuffix = String.valueOf(Year.now().getValue()).substring(2);
		String valueYearSuffix = value.substring(value.length() - 2);

		return valueYearSuffix.equals(currentYearSuffix);
	}

}
