
package acme.entities.activityLog;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
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
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "activity_log", indexes = {

	@Index(name = "idx_al_flight_assignment", columnList = "flight_assignment_id"), @Index(name = "idx_al_regm_sev", columnList = "registrationMoment,severityLevel")
})

public class ActivityLog extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	Date						registrationMoment;

	@Mandatory
	@ValidString(max = 50, min = 1)
	@Automapped
	String						typeOfIncident;

	@Mandatory
	@ValidString(max = 255, min = 1)
	@Automapped
	String						description;

	@Mandatory
	@ValidNumber(min = 0, max = 10)
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
