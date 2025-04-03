
package acme.features.customer.passenger;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.Passenger;
import acme.entities.booking.Booking;
import acme.features.customer.passengerBooking.PassengerBookingRepository;
import acme.realms.Customer;

@GuiService
public class CustomerPassengerListService extends AbstractGuiService<Customer, Passenger> {

	@Autowired
	private PassengerBookingRepository	passengerBookingRepository;

	@Autowired
	private CustomerPassengerRepository	repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int bookingId = super.getRequest().getData("id", int.class);
		int userId;
		int customerId;
		Booking booking = this.repository.findBookingById(bookingId);

		userId = super.getRequest().getPrincipal().getAccountId();
		customerId = this.repository.findCustomerIdByUserId(userId);

		if (bookingId == 0)
			status = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);
		else
			status = booking != null && customerId == booking.getCustomer().getId();

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int userAccountId = super.getRequest().getPrincipal().getAccountId();
		int customerId = this.repository.findCustomerIdByUserId(userAccountId);

		Collection<Passenger> data;
		int id = super.getRequest().getData("id", int.class);
		if (id == 0)
			data = this.repository.findAllByCustomer(customerId);
		else
			data = this.passengerBookingRepository.findPassengersByBookingId(id);

		super.getBuffer().addData(data);
	}

	@Override
	public void unbind(final Passenger passenger) {

		assert passenger != null;
		boolean showCreate;

		Dataset dataset;
		dataset = super.unbindObject(passenger, "fullName", "passportNumber", "specialNeeds", "email");
		showCreate = super.getRequest().getPrincipal().hasRealm(passenger.getCustomer());

		super.getResponse().addGlobal("showCreate", showCreate);

		super.getResponse().addData(dataset);

	}
}
