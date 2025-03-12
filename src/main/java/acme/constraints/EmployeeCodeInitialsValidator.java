
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import acme.client.components.basis.AbstractRole;
import acme.client.components.principals.UserAccount;
import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.entities.agent.AssistanceAgent;
import acme.entities.flightCrewMember.FlightCrewMember;
import acme.entities.manager.AirlineManager;

@Validator
public class EmployeeCodeInitialsValidator extends AbstractValidator<ValidEmployeeCodeInitials, AbstractRole> {

	private static final String EMPLOYEE_ID_PATTERN = "^[A-Z]{2,3}\\d{6}$";


	@Override
	public boolean isValid(final AbstractRole role, final ConstraintValidatorContext context) {
		assert context != null; // Verificar que el contexto no sea nulo

		boolean isValid = true; // Valor predeterminado de validez
		String attributeName = "identifier"; // Atributo correcto para `super.state`

		if (role == null) {
			super.state(context, false, attributeName, "Employee role cannot be null.");
			return false;
		}

		String identifierNumber = this.extractIdentifier(role);
		if (identifierNumber == null) {
			super.state(context, false, attributeName, "Identifier cannot be null.");
			return false;
		}

		if (!identifierNumber.matches(EmployeeCodeInitialsValidator.EMPLOYEE_ID_PATTERN)) {
			super.state(context, false, attributeName, "Invalid identifier format.");
			return false;
		}

		UserAccount userAccount = role.getUserAccount();
		if (userAccount == null || userAccount.getIdentity() == null) {
			super.state(context, false, attributeName, "User identity is missing.");
			return false;
		}

		String name = userAccount.getIdentity().getName();
		String surname = userAccount.getIdentity().getSurname();
		if (name == null || surname == null) {
			super.state(context, false, attributeName, "User's name and surname must not be null.");
			return false;
		}

		String initials = this.extractInitials(name, surname);
		String identifierPrefix = identifierNumber.substring(0, identifierNumber.length() - 6);

		if (!identifierPrefix.equals(initials)) {
			super.state(context, false, attributeName, "Identifier must start with the user's initials followed by six digits.");
			isValid = false;
		}

		return isValid;
	}

	private String extractIdentifier(final AbstractRole role) {
		if (role instanceof AirlineManager)
			return ((AirlineManager) role).getIdentifierNumber();
		else if (role instanceof AssistanceAgent)
			return ((AssistanceAgent) role).getEmployeeCode();
		else if (role instanceof FlightCrewMember)
			return ((FlightCrewMember) role).getEmployeeCode();
		return null;
	}

	private String extractInitials(final String name, final String surname) {
		StringBuilder initials = new StringBuilder();

		if (!name.isEmpty())
			initials.append(name.charAt(0));

		String[] surnameParts = surname.split("\\s+");
		if (surnameParts.length > 0 && !surnameParts[0].isEmpty())
			initials.append(surnameParts[0].charAt(0));
		if (surnameParts.length > 1 && !surnameParts[1].isEmpty())
			initials.append(surnameParts[1].charAt(0));

		return initials.toString().toUpperCase();
	}
}
