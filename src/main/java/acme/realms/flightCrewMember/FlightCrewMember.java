
package acme.realms.flightCrewMember;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.Valid;

import acme.client.components.basis.AbstractRole;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.constraints.ValidCodeInitials;
import acme.constraints.ValidUniqueEmployeeCode;
import acme.entities.airline.Airline;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@ValidCodeInitials
public class FlightCrewMember extends AbstractRole {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidString(pattern = "^[A-Z]{2,3}\\d{6}$")
	@ValidUniqueEmployeeCode
	@Column(unique = true)
	private String				employeeCode;

	@Mandatory
	@ValidString(pattern = "^\\+?\\d{6,15}$")
	@Automapped
	private String				phoneNumber;

	@Mandatory
	@ValidString(max = 255, min = 1)
	@Automapped
	String						languageSkills;

	@Mandatory
	@ValidString(pattern = "^(AVAILABLE|ON VACATION|ON LEAVE)$")
	@Automapped
	String						availabilityStatus;

	@Mandatory
	@ValidMoney(min = 0, max = 100000)
	@Automapped
	Money						salary;

	@Optional
	@ValidNumber(min = 0, max = 90)
	@Automapped
	Integer						yearsOfExperience;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	Airline						airline;

}
