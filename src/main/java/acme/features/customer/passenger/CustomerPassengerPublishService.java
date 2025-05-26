
package acme.features.customer.passenger;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.Passenger;
import acme.realms.Customer;

@GuiService
public class CustomerPassengerPublishService extends AbstractGuiService<Customer, Passenger> {

	@Autowired
	private CustomerPassengerRepository repository;


	@Override
	public void authorise() {
		int id = super.getRequest().getData("id", int.class);
		Passenger passenger = this.repository.findById(id);

		boolean authorised = passenger != null && !passenger.getPublished() && super.getRequest().getPrincipal().hasRealm(passenger.getCustomer());

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		Passenger passenger = this.repository.findById(id);
		super.getBuffer().addData(passenger);
	}

	@Override
	public void validate(final Passenger passenger) {

	}

	@Override
	public void bind(final Passenger passenger) {
		// No binding necesario para publicación
	}

	@Override
	public void unbind(final Passenger passenger) {

		assert passenger != null;

		Dataset dataset;
		dataset = super.unbindObject(passenger, "fullName", "passportNumber", "specialNeeds", "email", "dateOfBirth", "published");

		super.getResponse().addData(dataset);
	}

	@Override
	public void perform(final Passenger passenger) {
		passenger.setPublished(true);
		this.repository.save(passenger);
	}

}
