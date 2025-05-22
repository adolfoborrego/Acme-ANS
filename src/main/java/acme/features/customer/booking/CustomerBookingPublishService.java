
package acme.features.customer.booking;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.Booking;
import acme.entities.booking.TravelClass;
import acme.entities.flight.Flight;
import acme.realms.Customer;

@GuiService
public class CustomerBookingPublishService extends AbstractGuiService<Customer, Booking> {

	@Autowired
	protected CustomerBookingRepository repository;


	@Override
	public void authorise() {
		int id = super.getRequest().getData("id", int.class);
		Booking booking = this.repository.findById(id);
		int flightId = super.getRequest().getData("flight", int.class);
		Flight flight = this.repository.findFlightById(flightId);

		boolean flightPublished = flight != null && flight.getSheduledDeparture() != null ? flight.getSheduledDeparture().after(MomentHelper.getCurrentMoment()) : true;
		boolean authorised = booking != null && !booking.getPublished() && super.getRequest().getPrincipal().hasRealm(booking.getCustomer());

		super.getResponse().setAuthorised(authorised && flightPublished);
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
		assert booking != null;
		SelectChoices flights = SelectChoices.from(this.repository.findAllFlights().stream().filter(x -> x.getPublished() && x.getSheduledDeparture() != null ? x.getSheduledDeparture().after(MomentHelper.getCurrentMoment()) : true).toList(), "tag",
			booking.getFlight());

		SelectChoices travelClasses = SelectChoices.from(TravelClass.class, booking.getTravelClass());

		Dataset dataset = super.unbindObject(booking, "travelClass", "lastNibble", "price", "locatorCode", "flight", "purchaseMoment");
		dataset.put("flights", flights);
		dataset.put("travelClasses", travelClasses);
		super.getResponse().addData(dataset);
	}
}
