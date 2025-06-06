
package acme.features.customer.booking;

import java.util.Date;

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
public class CustomerBookingCreateService extends AbstractGuiService<Customer, Booking> {

	@Autowired
	private CustomerBookingRepository repository;


	@Override
	public void authorise() {
		boolean status;
		int flightId = 0;
		if (super.getRequest().hasData("flight"))
			flightId = super.getRequest().getData("flight", int.class);

		Flight flight = this.repository.findFlightById(flightId);

		boolean flightPublished = true;
		boolean flightAfterNow = true;

		if (flight != null) {
			flightPublished = flight.getPublished();
			flightAfterNow = flight.getSheduledDeparture() != null ? flight.getSheduledDeparture().after(MomentHelper.getCurrentMoment()) : true;
		}

		status = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);
		super.getResponse().setAuthorised(status && flightPublished && flightAfterNow);
	}

	@Override
	public void load() {
		Booking object = new Booking();
		Date purchaseMoment = MomentHelper.getCurrentMoment();
		object.setPurchaseMoment(purchaseMoment);
		object.setPublished(false);
		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final Booking object) {
		assert object != null;
		super.bindObject(object, "travelClass", "price", "locatorCode", "flight", "lastNibble");
	}

	@Override
	public void validate(final Booking object) {
		assert object != null;

		boolean flightExistAndIsPublished = object.getFlight() != null;
		super.state(flightExistAndIsPublished, "flight", "customer.booking.error.dontExist-flight");

		boolean locatorCodeIsUnique = !this.repository.findBookingsWhithoutBookingId(object.getId()).stream().map(x -> x.getLocatorCode()).anyMatch(x -> x.equals(object.getLocatorCode()));
		super.state(locatorCodeIsUnique, "flight", "customer.booking.error.locator-NorUnique");

	}

	@Override
	public void perform(final Booking object) {
		assert object != null;

		int userAccountId = super.getRequest().getPrincipal().getAccountId();
		int customerId = this.repository.findCustomerIdByUserId(userAccountId);

		Customer customer = new Customer();
		customer.setId(customerId);

		object.setCustomer(customer);
		this.repository.save(object);
	}

	@Override
	public void unbind(final Booking object) {
		assert object != null;
		SelectChoices flights = SelectChoices.from(
			this.repository.findAllFlights().stream().filter(x -> x.getPublished().equals(true)).filter(x -> x.getSheduledDeparture() != null ? x.getSheduledDeparture().after(MomentHelper.getCurrentMoment()) : true).toList(), "tag", object.getFlight());
		SelectChoices travelClasses = SelectChoices.from(TravelClass.class, object.getTravelClass());

		Date purchaseMoment = MomentHelper.getCurrentMoment();

		Dataset dataset = super.unbindObject(object, "travelClass", "price", "locatorCode", "lastNibble");
		dataset.put("flights", flights);
		dataset.put("travelClasses", travelClasses);
		dataset.put("purchaseMoment", purchaseMoment);
		super.getResponse().addData(dataset);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equalsIgnoreCase("POST"))
			PrincipalHelper.handleUpdate();
	}
}
