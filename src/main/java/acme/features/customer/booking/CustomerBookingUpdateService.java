
package acme.features.customer.booking;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.Booking;
import acme.entities.booking.TravelClass;
import acme.entities.flight.Flight;
import acme.realms.Customer;

@GuiService
public class CustomerBookingUpdateService extends AbstractGuiService<Customer, Booking> {

	@Autowired
	protected CustomerBookingRepository repository;


	@Override
	public void authorise() {
		int id = super.getRequest().getData("id", int.class);
		int flightId = super.getRequest().hasData("flight") ? super.getRequest().getData("flight", int.class) : 0;
		Flight flight = this.repository.findFlightById(flightId);
		Booking booking = this.repository.findById(id);

		boolean flightPublished = flight != null && flight.getSheduledDeparture() != null ? flight.getSheduledDeparture().after(MomentHelper.getCurrentMoment()) : true;
		boolean authorised = booking != null && super.getRequest().getPrincipal().hasRealm(booking.getCustomer()) && !booking.getPublished();

		super.getResponse().setAuthorised(authorised && flightPublished);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		Booking booking = this.repository.findById(id);
		super.getBuffer().addData(booking);
	}

	@Override
	public void bind(final Booking booking) {
		assert booking != null;
		super.bindObject(booking, "travelClass", "price", "locatorCode", "flight", "lastNibble");
	}

	@Override
	public void validate(final Booking booking) {
		assert booking != null;

		List<String> locatorsYaExistentes = this.repository.findBookingsWhithoutBookingId(booking.getId()).stream().map(x -> x.getLocatorCode()).toList();
		boolean locatorCodeIsUnique = !locatorsYaExistentes.contains(booking.getLocatorCode());
		boolean flightIsSelected = booking.getFlight() != null;
		super.state(flightIsSelected, "flight", "customer.booking.error.flight-notNull");
		super.state(locatorCodeIsUnique, "locatorCode", "customer.booking.error.locator-NorUnique");

	}

	@Override
	public void perform(final Booking booking) {
		assert booking != null;
		this.repository.save(booking);
	}

	@Override
	public void unbind(final Booking booking) {
		assert booking != null;
		SelectChoices flights = SelectChoices.from(
			this.repository.findAllFlights().stream().filter(x -> x.getPublished().equals(true)).filter(x -> x.getSheduledDeparture() != null ? x.getSheduledDeparture().after(MomentHelper.getCurrentMoment()) : true).toList(), "tag", booking.getFlight());

		SelectChoices travelClasses = SelectChoices.from(TravelClass.class, booking.getTravelClass());

		Dataset dataset = super.unbindObject(booking, "travelClass", "price", "locatorCode", "purchaseMoment", "lastNibble");
		dataset.put("numberOfLayovers", booking.getNumberOfLayovers());
		dataset.put("travelClasses", travelClasses);
		dataset.put("flights", flights);
		super.getResponse().addData(dataset);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equalsIgnoreCase("POST"))
			PrincipalHelper.handleUpdate();
	}

}
