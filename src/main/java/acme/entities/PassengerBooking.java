
package acme.entities;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Optional;
import acme.entities.booking.Booking;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(indexes = {
	@Index(columnList = "booking_id")
})
public class PassengerBooking extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Optional
	@Automapped
	@Valid
	@ManyToOne
	private Booking				booking;

	@Optional
	@Automapped
	@Valid
	@ManyToOne
	private Passenger			passenger;

}
