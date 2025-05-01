
package acme.entities.leg;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
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
	@Valid
	@Automapped
	@Enumerated(EnumType.STRING)
	private LegStatus			status;

	@Mandatory
	@Automapped
	private Boolean				published;

	// Derived attributes -----------------------------------------------------


	@Transient
	public Double getDuration() {
		if (this.getScheduledDeparture() == null || this.getScheduledArrival() == null)
			return null;

		Duration duration = MomentHelper.computeDuration(this.getScheduledDeparture(), this.getScheduledArrival());
		return duration.getSeconds() / 60.;
	}

	@Transient
	public String getFlightNumber() {
		if (this.aircraft == null || this.aircraft.getAirline() == null)
			return null;

		String iataCode = this.aircraft.getAirline().getIataCode();
		String suffix = String.format("%04d", this.getId() % 10000);
		return iataCode + suffix;
	}


	@Transient
	private String identificator;


	/**
	 * Getter que construye dinámicamente la cadena deseada.
	 */
	@Transient
	public String getIdentificator() {
		// tu implementación actual:
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String depCode = this.departureAirport != null ? this.departureAirport.getIataCode() : "N/A";
		String arrCode = this.arrivalAirport != null ? this.arrivalAirport.getIataCode() : "N/A";
		String flightNum = this.getFlightNumber() != null ? this.getFlightNumber() : "N/A";
		String dateStr = this.scheduledDeparture != null ? fmt.format(this.scheduledDeparture) : "N/A";
		return String.format("From: %s- To: %s, On FlightNumber: %s, At: %s", depCode, arrCode, flightNum, dateStr);
	}

	/**
	 * Setter vacío para que ReflectionHelper detecte la propiedad 'identificator'.
	 */
	public void setIdentificator(final String identificator) {
		// no hace nada, o podrías cachear el valor si lo deseas
		this.identificator = identificator;
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
