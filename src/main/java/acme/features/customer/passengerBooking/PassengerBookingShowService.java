
package acme.features.customer.passengerBooking;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.PassengerBooking;
import acme.realms.Customer;

@GuiService
public class PassengerBookingShowService extends AbstractGuiService<Customer, PassengerBooking> {

	@Autowired
	private PassengerBookingRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int passengerBookingId;
		int userId;
		int customerId;
		PassengerBooking passengerBooking;

		userId = super.getRequest().getPrincipal().getAccountId();
		customerId = this.repository.findCustomerIdByUserId(userId);
		passengerBookingId = super.getRequest().getData("id", int.class);
		passengerBooking = this.repository.findPassengersBookingById(passengerBookingId);

		status = passengerBooking != null && customerId == passengerBooking.getBooking().getCustomer().getId();
		super.getResponse().setAuthorised(status);

	}

	@Override
	public void load() {
		int passengerBookingId;
		PassengerBooking passengerBooking;

		passengerBookingId = super.getRequest().getData("id", int.class);
		passengerBooking = this.repository.findPassengersBookingById(passengerBookingId);

		super.getBuffer().addData(passengerBooking);
	}

	@Override
	public void unbind(final PassengerBooking passengerBooking) {

		assert passengerBooking != null;

		Dataset dataset;
		dataset = super.unbindObject(passengerBooking, "booking", "passenger");

		super.getResponse().addData(dataset);
	}

}
