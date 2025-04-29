
package acme.entities.booking;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidString;
import acme.client.helpers.SpringHelper;
import acme.entities.flight.Flight;
import acme.features.customer.passengerBooking.PassengerBookingRepository;
import acme.realms.Customer;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Booking extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidString(pattern = "^[A-Z0-9]{6,8}$")
	@Column(unique = true)
	private String				locatorCode;

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.DATE)
	private Date				purchaseMoment;

	@Mandatory
	@Enumerated(EnumType.STRING)
	@Automapped
	private TravelClass			travelClass;

	@Mandatory
	@ValidMoney(min = 0.00, max = 999999999999.99)
	@Automapped
	private Money				price;

	@Optional
	@ValidString(pattern = "^\\d{4}$")
	@Automapped
	private String				lastNibble;

	@Optional
	@Valid
	@Automapped
	@ManyToOne(optional = false)
	private Customer			customer;

	@Optional
	@Valid
	@Automapped
	@ManyToOne(optional = false)
	private Flight				flight;

	@Mandatory
	@Automapped
	private Boolean				published;

	// Derived attributes -----------------------------------------------------


	@Transient
	public Integer getNumberOfLayovers() {
		Integer result;
		PassengerBookingRepository passengerBookingRepository;
		passengerBookingRepository = SpringHelper.getBean(PassengerBookingRepository.class);
		result = passengerBookingRepository.countNumberOfPassengersOfBooking(this.getId());
		return result;
	}

}
