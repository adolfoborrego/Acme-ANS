
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
import acme.realms.Customer;

@GuiService
public class CustomerBookingCreateService extends AbstractGuiService<Customer, Booking> {

	@Autowired
	private CustomerBookingRepository repository;


	@Override
	public void authorise() {
		boolean status;

		status = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Booking object = new Booking();
		object.setPublished(false);
		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final Booking object) {
		assert object != null;
		super.bindObject(object, "travelClass", "price", "locatorCode", "flight", "lastNibble", "purchaseMoment");
	}

	@Override
	public void validate(final Booking object) {
		assert object != null;

		boolean flightExistAndIsPublished = object.getFlight() != null ? object.getFlight().getPublished() == true : false;
		super.state(flightExistAndIsPublished, "flight", object.getFlight() != null ? "customer.booking.error.flight-notPublished" : "customer.booking.error.dontExist-flight");
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

		SelectChoices flights;
		if (this.repository.findAllFlights().stream().filter(x -> x.getPublished() == true).toList().contains(object.getFlight()))
			flights = SelectChoices.from(this.repository.findAllFlights().stream().filter(x -> x.getPublished() == true).toList(), "id", object.getFlight());
		else
			flights = SelectChoices.from(this.repository.findAllFlights().stream().filter(x -> x.getPublished() == true).toList(), "id", null);

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
