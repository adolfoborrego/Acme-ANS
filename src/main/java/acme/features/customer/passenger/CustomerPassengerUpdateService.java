
package acme.features.customer.passenger;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.Passenger;
import acme.realms.Customer;

@GuiService
public class CustomerPassengerUpdateService extends AbstractGuiService<Customer, Passenger> {

	@Autowired
	protected CustomerPassengerRepository repository;


	@Override
	public void authorise() {
		int id = super.getRequest().getData("id", int.class);
		Passenger passenger = this.repository.findById(id);
		boolean authorised = passenger != null && super.getRequest().getPrincipal().hasRealm(passenger.getCustomer()) && !passenger.getPublished();

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		Passenger passenger = this.repository.findById(id);
		super.getBuffer().addData(passenger);
	}

	@Override
	public void bind(final Passenger passenger) {
		assert passenger != null;
		super.bindObject(passenger, "fullName", "passportNumber", "specialNeeds", "email", "dateOfBirth");
	}

	@Override
	public void validate(final Passenger passenger) {
		assert passenger != null;

	}

	@Override
	public void perform(final Passenger passenger) {
		assert passenger != null;
		this.repository.save(passenger);
	}

	@Override
	public void unbind(final Passenger passenger) {
		assert passenger != null;

		Dataset dataset = super.unbindObject(passenger, "fullName", "passportNumber", "specialNeeds", "email", "dateOfBirth");

		super.getResponse().addData(dataset);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equalsIgnoreCase("POST"))
			PrincipalHelper.handleUpdate();
	}
}
