
package acme.features.customer.booking;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.Booking;
import acme.entities.booking.TravelClass;
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
		Date now = MomentHelper.getCurrentMoment();

		SelectChoices flights = SelectChoices.from(
			this.repository.findAllFlights().stream().filter(x -> x.getPublished().equals(true)).filter(x -> x.getSheduledDeparture() != null ? x.getSheduledDeparture().after(MomentHelper.getCurrentMoment()) : true).toList(), "tag", booking.getFlight());

		SelectChoices travelClasses = SelectChoices.from(TravelClass.class, booking.getTravelClass());

		Dataset dataset;
		dataset = super.unbindObject(booking, "travelClass", "price", "locatorCode", "flight", "purchaseMoment", "lastNibble", "published");
		dataset.put("numberOfLayovers", booking.getNumberOfLayovers());
		dataset.put("travelClasses", travelClasses);
		dataset.put("flights", flights);

		super.getResponse().addData(dataset);
	}
}
