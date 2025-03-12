
package acme.entities.leg;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidString;
import acme.client.helpers.SpringHelper;
import acme.entities.Aircraft;
import acme.entities.Flight;
import acme.entities.airport.Airport;
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
	@DecimalMin("0.0")
	@Automapped
	private Double				hours;

	@Mandatory
	@ValidString(pattern = "^(ON-TIME|DELAYED|CANCELLED|LANDED)$")
	@Automapped
	private String				status;

	// Derived attributes -----------------------------------------------------


	@Transient
	public Date getSheduledDeparture() {
		Date result;
		Leg leg;
		LegRepository legRepository;
		legRepository = SpringHelper.getBean(LegRepository.class);
		leg = legRepository.findFirstLegByFlight(this.getId());
		result = leg.getScheduledDeparture();
		return result;
	}

	@Transient
	public Date getSheduledArrival() {
		Date result;
		Leg leg;
		LegRepository legRepository;
		legRepository = SpringHelper.getBean(LegRepository.class);
		leg = legRepository.findLastLegByFlight(this.getId());
		result = leg.getScheduledArrival();
		return result;
	}

	@Transient
	public String getOriginCity() {
		String result;
		Leg leg;
		LegRepository legRepository;
		legRepository = SpringHelper.getBean(LegRepository.class);
		leg = legRepository.findFirstLegByFlight(this.getId());
		result = leg.getDepartureAirport().getCity();
		return result;
	}

	@Transient
	public String getDestinationCity() {
		String result;
		Leg leg;
		LegRepository legRepository;
		legRepository = SpringHelper.getBean(LegRepository.class);
		leg = legRepository.findLastLegByFlight(this.getId());
		result = leg.getArrivalAirport().getCity();
		return result;
	}

	@Transient
	public Integer getNumberOfLayovers() {
		Integer result;
		LegRepository legRepository;
		legRepository = SpringHelper.getBean(LegRepository.class);
		result = legRepository.countNumberOfLegsOfFlight(this.getId());
		return result;
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
