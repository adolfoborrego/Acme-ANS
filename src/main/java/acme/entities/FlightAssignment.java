
package acme.entities;

import java.util.Date;

import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidString;

public class FlightAssignment extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidString(pattern = "^(PILOT|COPILOT|LEAD ATTENDANT|CABIN ATTENDANT)$")
	String						duty;

	@Mandatory
	@Temporal(TemporalType.DATE)
	@ValidMoment(past = true)
	Date						momentOfLastUpdate;

	@Mandatory
	@ValidString(pattern = "^(CONFIRMED||PENDING|CANCELLED)$")
	String						currentStatus;

	@Optional
	@ValidString(max = 255)
	String						remarks;

	@ManyToOne
	@Valid
	FlightCrewMember			assignTo;

	@ManyToOne
	@Valid
	Leg							associatedTo;
}
