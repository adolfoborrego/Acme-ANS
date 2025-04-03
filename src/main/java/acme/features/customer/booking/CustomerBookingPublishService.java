
package acme.features.customer.booking;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.Booking;
import acme.entities.booking.TravelClass;
import acme.realms.Customer;

@GuiService
public class CustomerBookingPublishService extends AbstractGuiService<Customer, Booking> {

	@Autowired
	protected CustomerBookingRepository repository;


	@Override
	public void authorise() {
		int id = super.getRequest().getData("id", int.class);
		Booking booking = this.repository.findById(id);

		boolean authorised = booking != null && !booking.getPublished() && super.getRequest().getPrincipal().hasRealm(booking.getCustomer());

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		Booking booking = this.repository.findById(id);
		super.getBuffer().addData(booking);
	}

	@Override
	public void validate(final Booking booking) {
		String lastNibble = booking.getLastNibble();

		boolean lastNibbleNotNull = !lastNibble.isBlank();

		super.state(lastNibbleNotNull, "published", "customer.booking.publish.error.no-lastNibble");
	}

	@Override
	public void bind(final Booking booking) {
		// No binding necesario para publicación
	}

	@Override
	public void perform(final Booking booking) {
		booking.setPublished(true);
		this.repository.save(booking);
	}

	@Override
	public void unbind(final Booking booking) {
		SelectChoices flights = SelectChoices.from(this.repository.findAllFlights(), "id", booking.getFlight());
		SelectChoices travelClasses = SelectChoices.from(TravelClass.class, booking.getTravelClass());

		Dataset dataset = super.unbindObject(booking, "travelClass", "price", "locatorCode", "flight", "published", "purchaseMoment");
		dataset.put("flights", flights);
		dataset.put("travelClasses", travelClasses);
		super.getResponse().addData(dataset);
	}
}
