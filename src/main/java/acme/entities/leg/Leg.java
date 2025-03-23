
package acme.entities.leg;

import java.time.Duration;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidString;
import acme.client.helpers.MomentHelper;
import acme.entities.aircraft.Aircraft;
import acme.entities.airport.Airport;
import acme.entities.flight.Flight;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Leg extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@Temporal(TemporalType.TIMESTAMP)
	@Automapped
	private Date				scheduledDeparture;

	@Mandatory
	@Temporal(TemporalType.TIMESTAMP)
	@Automapped
	private Date				scheduledArrival;

	@Mandatory
	@ValidString(pattern = "^(ON-TIME|DELAYED|CANCELLED|LANDED)$")
	@Automapped
	private String				status;

	// Derived attributes -----------------------------------------------------


	@Transient
	private Double getDuration() {
		Duration duration = MomentHelper.computeDuration(this.getScheduledDeparture(), this.getScheduledArrival());

		return duration.getSeconds() / 60.;
	}

	@Transient
	private String getFlightNumber() {
		String iataCode = this.aircraft.getAirline().getIataCode();

		String suffix = String.format("%04d", this.getId() % 10000);

		return iataCode + suffix;
	}

	// Relationships ----------------------------------------------------------


	@Mandatory
	@ManyToOne
	@Valid
	private Airport		departureAirport;

	@Optional
	@ManyToOne
	@Valid
	private Airport		arrivalAirport;

	@Optional
	@ManyToOne
	@Valid
	private Aircraft	aircraft;

	@Optional
	@ManyToOne
	@Valid
	private Flight		flight;

}
