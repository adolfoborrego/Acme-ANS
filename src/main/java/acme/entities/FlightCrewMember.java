
package acme.entities;

import javax.persistence.Column;
import javax.persistence.ManyToOne;

import acme.client.components.basis.AbstractRole;
import acme.client.components.datatypes.Money;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;

public class FlightCrewMember extends AbstractRole {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@Column(unique = true)
	@ValidString(pattern = "^[A-Z]{2-3}\\d{6}$")
	private String				employeeCode;

	@Mandatory
	@ValidString(pattern = "^\\+?\\d{6,15}$")
	private String				phoneNumber;

	@Mandatory
	@ValidString(max = 255)
	String						languageSkills;

	@Mandatory
	@ValidString(pattern = "^(AVAILABLE|ON VACATION|ON LEAVE)$")
	String						availabilityStatus;

	@Mandatory
	@ManyToOne
	Airline						airline;

	@Mandatory
	@ValidMoney
	Money						salary;

	@Optional
	@ValidNumber(min = 0, max = 90)
	Integer						yearsOfExperience;

}
