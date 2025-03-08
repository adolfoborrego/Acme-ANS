
package acme.entities;

import java.util.Date;

import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;

public class ActivityLog extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@ManyToOne
	@Mandatory
	@Valid
	FlightAssignment			associatedTo;

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.DATE)
	Date						registrationMoment;

	@Mandatory
	@ValidString(max = 50)
	String						typeOfIncident;

	@Mandatory
	@ValidString(max = 250)
	String						description;

	@Mandatory
	@ValidNumber(min = 0, max = 10, fraction = 0)
	@NotNull
	Integer						severityLevel;

	@Mandatory
	@NotNull
	Boolean						isPublished;

}
