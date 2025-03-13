
package acme.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidString;
import acme.client.helpers.SpringHelper;
import acme.entities.leg.Leg;
import acme.entities.leg.LegRepository;
import acme.entities.manager.AirlineManager;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Flight extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@ValidString(max = 50)
	@Automapped
	private String				tag;

	@Mandatory
	@Automapped
	private Boolean				indicator;

	@Mandatory
	@ValidMoney(min = 0)
	@Automapped
	private Money				cost;

	@Optional
	@ValidString(max = 255)
	@Automapped
	private String				description;

	@Mandatory
	@Automapped
	private Boolean				published;

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
	public String getDepartureCity() {
		String result;
		Leg leg;
		LegRepository legRepository;
		legRepository = SpringHelper.getBean(LegRepository.class);
		leg = legRepository.findFirstLegByFlight(this.getId());
		result = leg.getDepartureAirport().getCity();
		return result;
	}

	@Transient
	public String getArrivalCity() {
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


	@Optional
	@ManyToOne
	@Valid
	private AirlineManager airlineManager;

}
