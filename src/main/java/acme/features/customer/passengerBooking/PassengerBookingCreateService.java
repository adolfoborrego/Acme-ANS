
package acme.features.customer.passengerBooking;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.PassengerBooking;
import acme.entities.booking.Booking;
import acme.realms.Customer;

@GuiService
public class PassengerBookingCreateService extends AbstractGuiService<Customer, PassengerBooking> {

	@Autowired
	protected PassengerBookingRepository repository;


	@Override
	public void authorise() {
		int bookingId = super.getRequest().getData("bookingId", int.class);
		Booking booking = this.repository.findBookingById(bookingId);

		boolean isOwner = false;
		boolean isNotPublished = false;

		if (booking != null) {
			int userAccountId = super.getRequest().getPrincipal().getAccountId();
			int customerId = this.repository.findCustomerIdByUserId(userAccountId);

			isOwner = booking.getCustomer().getId() == customerId;
			isNotPublished = !booking.getPublished();
		}

		super.getResponse().setAuthorised(isOwner && isNotPublished);
	}

	@Override
	public void load() {
		int bookingId = super.getRequest().getData("bookingId", int.class);
		Booking booking = this.repository.findBookingById(bookingId);

		PassengerBooking passengerBooking = new PassengerBooking();
		passengerBooking.setBooking(booking);

		super.getBuffer().addData(passengerBooking);
	}

	@Override
	public void validate(final PassengerBooking passengerBooking) {
		assert passengerBooking != null;

		int bookingId = super.getRequest().getData("bookingId", int.class);
		Booking booking = this.repository.findBookingById(bookingId);

		boolean passengerCustomerSameBooking = passengerBooking.getPassenger() != null ? passengerBooking.getPassenger().getCustomer() == booking.getCustomer() : false;
		super.state(passengerCustomerSameBooking, "passenger", passengerBooking.getPassenger() != null ? "customer.passenger-booking.error.different-customer" : "customer.passenger-booking.error.dontExist-passenger");

	}

	@Override
	public void bind(final PassengerBooking passengerBooking) {
		assert passengerBooking != null;
		super.bindObject(passengerBooking, "passenger");
	}

	@Override
	public void perform(final PassengerBooking passengerBooking) {
		this.repository.save(passengerBooking);
	}

	@Override
	public void unbind(final PassengerBooking passengerBooking) {
		assert passengerBooking != null;

		SelectChoices passengers;

		if (this.repository.findPassengersByCustomerId(passengerBooking.getBooking().getCustomer().getId()).contains(passengerBooking.getPassenger()))
			passengers = SelectChoices.from(this.repository.findPassengersByCustomerId(passengerBooking.getBooking().getCustomer().getId()), "id", passengerBooking.getPassenger());
		else
			passengers = SelectChoices.from(this.repository.findPassengersByCustomerId(passengerBooking.getBooking().getCustomer().getId()), "id", null);

		Dataset dataset = super.unbindObject(passengerBooking);
		dataset.put("bookingId", passengerBooking.getBooking().getId());
		dataset.put("passengers", passengers);

		super.getResponse().addData(dataset);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equals("POST"))
			PrincipalHelper.handleUpdate();
	}
}
