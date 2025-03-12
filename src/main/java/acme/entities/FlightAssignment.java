
package acme.entities;

import java.util.Date;

import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidString;
import acme.entities.member.FlightCrewMember;

public class FlightAssignment extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidString(pattern = "^(PILOT|COPILOT|LEAD ATTENDANT|CABIN ATTENDANT)$")
	@NotNull
	String						duty;

	@Mandatory
	@Temporal(TemporalType.DATE)
	@ValidMoment(past = true)
	@NotNull
	Date						momentOfLastUpdate;

	@Mandatory
	@ValidString(pattern = "^(CONFIRMED||PENDING|CANCELLED)$")
	@NotNull
	String						currentStatus;

	@Optional
	@ValidString(max = 255)
	@NotNull
	String						remarks;

	@ManyToOne
	@Valid
	@Mandatory
	FlightCrewMember			assignTo;

	@ManyToOne
	@Valid
	@Mandatory
	Leg							associatedTo;
}
