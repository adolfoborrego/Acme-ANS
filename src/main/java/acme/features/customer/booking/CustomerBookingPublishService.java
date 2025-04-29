
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

		boolean hasPassengers = !this.repository.findPassengersByBookingId(booking.getId()).isEmpty();

		boolean lastNibbleNotNull = !lastNibble.isBlank();

		super.state(lastNibbleNotNull, "published", "customer.booking.publish.error.no-lastNibble");
		super.state(hasPassengers, "*", "customer.booking.publish.error.no-Passengers");

		boolean locatorCodeIsUnique = !this.repository.findBookingsWhithoutBookingId(booking.getId()).stream().map(x -> x.getLocatorCode()).anyMatch(x -> x.equals(booking.getLocatorCode()));
		super.state(locatorCodeIsUnique, "flight", "customer.booking.error.locator-NorUnique");

		boolean flightExistAndIsPublished = booking.getFlight() != null;
		super.state(flightExistAndIsPublished, "flight", "customer.booking.error.dontExist-flight");

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
		SelectChoices flights;
		if (this.repository.findAllFlights().stream().filter(x -> x.getPublished() == true).toList().contains(booking.getFlight()))
			flights = SelectChoices.from(this.repository.findAllFlights().stream().filter(x -> x.getPublished() == true).toList(), "tag", booking.getFlight());
		else
			flights = SelectChoices.from(this.repository.findAllFlights().stream().filter(x -> x.getPublished() == true).toList(), "tag", null);
		SelectChoices travelClasses = SelectChoices.from(TravelClass.class, booking.getTravelClass());

		Dataset dataset = super.unbindObject(booking, "travelClass", "lastNibble", "price", "locatorCode", "flight", "published", "purchaseMoment");
		dataset.put("flights", flights);
		dataset.put("travelClasses", travelClasses);
		super.getResponse().addData(dataset);
	}
}
