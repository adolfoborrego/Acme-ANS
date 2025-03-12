
package acme.entities.flightAssignment;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidString;
import acme.entities.flightCrewMember.FlightCrewMember;
import acme.entities.leg.Leg;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class FlightAssignment extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidString(pattern = "^(PILOT|COPILOT|LEAD ATTENDANT|CABIN ATTENDANT)$")
	@Automapped
	String						duty;

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	Date						momentOfLastUpdate;

	@Mandatory
	@ValidString(pattern = "^(CONFIRMED||PENDING|CANCELLED)$")
	@Automapped
	String						currentStatus;

	@Optional
	@ValidString(max = 255)
	@Automapped
	String						remarks;

	@Mandatory
	@Valid
	@Automapped
	@ManyToOne(optional = false)
	FlightCrewMember			flightCrewMember;

	@Mandatory
	@Valid
	@Automapped
	@ManyToOne /* ( optional = false ) */
	Leg							leg;
}
