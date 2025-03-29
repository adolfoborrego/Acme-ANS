
package acme.features.customer.passenger;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.Passenger;
import acme.features.customer.passengerBooking.PassengerBookingRepository;
import acme.realms.Customer;

@GuiService
public class CustomerPassengerListService extends AbstractGuiService<Customer, Passenger> {

	@Autowired
	private PassengerBookingRepository passengerBookingRepository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		status = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Collection<Passenger> data;
		int bookingId = super.getRequest().getData("bookingId", int.class);

		data = this.passengerBookingRepository.findPassengersByBookingId(bookingId);

		super.getBuffer().addData(data);
	}

	@Override
	public void unbind(final Passenger passenger) {

		assert passenger != null;

		Dataset dataset;
		dataset = super.unbindObject(passenger, "fullName", "passportNumber", "specialNeeds", "email");

		super.getResponse().addData(dataset);
		;
	}
}
