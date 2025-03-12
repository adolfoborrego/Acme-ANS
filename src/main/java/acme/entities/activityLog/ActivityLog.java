
package acme.entities.activityLog;

import java.util.Date;

import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.entities.flightAssignment.FlightAssignment;

public class ActivityLog extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.DATE)
	Date						registrationMoment;

	@Mandatory
	@ValidString(max = 50)
	@Automapped
	String						typeOfIncident;

	@Mandatory
	@ValidString(max = 250)
	@Automapped
	String						description;

	@Mandatory
	@ValidNumber(min = 0, max = 10, fraction = 0)
	@Automapped
	Integer						severityLevel;

	@Mandatory
	@Automapped
	Boolean						isPublished;

	@Mandatory
	@Valid
	@Automapped
	@ManyToOne(optional = false)
	FlightAssignment			flightAssignment;

}
