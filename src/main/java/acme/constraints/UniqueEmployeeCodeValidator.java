
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.realms.airlineManager.AirlineManagerRepository;
import acme.realms.assistanceAgent.AssistanceAgentRepository;
import acme.realms.flightCrewMember.FlightCrewMemberRepository;

@Validator
public class UniqueEmployeeCodeValidator extends AbstractValidator<ValidUniqueEmployeeCode, String> {

	@Autowired
	AirlineManagerRepository	airlineManagerRepository;

	@Autowired
	FlightCrewMemberRepository	flightCrewMemberRepository;

	@Autowired
	AssistanceAgentRepository	assistanceAgentRepository;


	@Override
	public boolean isValid(final String value, final ConstraintValidatorContext context) {
		if (value == null || value.trim().isEmpty())
			return false;

		Boolean existsInManager = this.airlineManagerRepository.existsManagerWithCode(value);
		Boolean existsInMember = this.flightCrewMemberRepository.existsMemberWithCode(value);
		Boolean existsInAgent = this.assistanceAgentRepository.existsAgentWithCode(value);

		return !(existsInManager || existsInAgent || existsInMember);
	}

}
