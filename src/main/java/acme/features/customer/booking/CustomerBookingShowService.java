
package acme.features.customer.booking;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.Booking;
import acme.realms.Customer;

@GuiService
public class CustomerBookingShowService extends AbstractGuiService<Customer, Booking> {

	@Autowired
	private CustomerBookingRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int bookingId;
		int userId;
		int customerId;
		Booking booking;

		userId = super.getRequest().getPrincipal().getAccountId();
		customerId = this.repository.findCustomerIdByUserId(userId);
		bookingId = super.getRequest().getData("id", int.class);
		booking = this.repository.findById(bookingId);

		status = booking != null && customerId == booking.getCustomer().getId();
		super.getResponse().setAuthorised(status);

	}

	@Override
	public void load() {
		int bookingId;
		Booking booking;

		bookingId = super.getRequest().getData("id", int.class);
		booking = this.repository.findById(bookingId);

		super.getBuffer().addData(booking);
	}

	@Override
	public void unbind(final Booking booking) {

		assert booking != null;

		Dataset dataset;
		dataset = super.unbindObject(booking, "travelClass", "price", "locatorCode", "flight", "purchaseMoment", "lastNibble");
		dataset.put("numberOfLayovers", booking.getNumberOfLayovers());

		super.getResponse().addData(dataset);
	}
}
