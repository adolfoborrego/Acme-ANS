
package acme.features.customer.passenger;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.Passenger;
import acme.realms.Customer;

@GuiService
public class CustomerPassengerCreateService extends AbstractGuiService<Customer, Passenger> {

	@Autowired
	protected CustomerPassengerRepository repository;


	@Override
	public void authorise() {
		boolean status;
		status = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {

		Passenger passenger = new Passenger();

		super.getBuffer().addData(passenger);
	}

	@Override
	public void validate(final Passenger passenger) {
		assert passenger != null;

	}

	@Override
	public void bind(final Passenger passenger) {
		assert passenger != null;
		super.bindObject(passenger, "fullName", "passportNumber", "specialNeeds", "email", "dateOfBirth");
	}

	@Override
	public void perform(final Passenger passenger) {
		int userAccountId = super.getRequest().getPrincipal().getAccountId();
		int customerId = this.repository.findCustomerIdByUserId(userAccountId);

		Customer customer = this.repository.findCustomerById(customerId);
		passenger.setCustomer(customer);

		this.repository.save(passenger);
	}

	@Override
	public void unbind(final Passenger passenger) {
		assert passenger != null;

		Dataset dataset = super.unbindObject(passenger, "fullName", "passportNumber", "specialNeeds", "email", "dateOfBirth");

		super.getResponse().addData(dataset);
	}

}
