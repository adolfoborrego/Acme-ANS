
package acme.features.customer.passengerBooking;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.Passenger;
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
		int passengerId = 0;
		if (super.getRequest().hasData("passenger"))
			passengerId = super.getRequest().getData("passenger", int.class);
		Booking booking = this.repository.findBookingById(bookingId);

		boolean isOwner = false;
		boolean isNotPublished = false;
		boolean passengerCustomer = true;
		boolean passengerNotPublished = true;

		if (booking != null) {
			if (passengerId != 0) {
				Passenger passenger = this.repository.findPassengerById(passengerId) == null ? null : this.repository.findPassengerById(passengerId);
				if (passenger != null) {
					passengerCustomer = booking.getCustomer().equals(passenger.getCustomer());
					passengerNotPublished = passenger.getPublished();
				}
			}

			int userAccountId = super.getRequest().getPrincipal().getAccountId();
			int customerId = this.repository.findCustomerIdByUserId(userAccountId);

			isOwner = booking.getCustomer().getId() == customerId;
			isNotPublished = !booking.getPublished();
		}

		super.getResponse().setAuthorised(isOwner && isNotPublished && passengerCustomer && passengerNotPublished);
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

		boolean passengerCustomerSameBooking = passengerBooking.getPassenger() != null;
		super.state(passengerCustomerSameBooking, "passenger", "customer.passenger-booking.error.dontExist-passenger");

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
			passengers = SelectChoices.from(this.repository.findPassengersByCustomerId(passengerBooking.getBooking().getCustomer().getId()).stream().filter(x -> x.getPublished() == true).toList(), "fullName", passengerBooking.getPassenger());
		else
			passengers = SelectChoices.from(this.repository.findPassengersByCustomerId(passengerBooking.getBooking().getCustomer().getId()).stream().filter(x -> x.getPublished() == true).toList(), "fullName", null);

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
