
package acme.features.customer.booking;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.booking.Booking;
import acme.realms.Customer;

@GuiService
public class CustomerBookingListService extends AbstractGuiService<Customer, Booking> {

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
		Collection<Booking> data;
		int userId;
		int customerId;

		userId = super.getRequest().getPrincipal().getAccountId();
		customerId = this.repository.findCustomerIdByUserId(userId);
		data = this.repository.findAllByCustomer(customerId);

		super.getBuffer().addData(data);
	}

	@Override
	public void unbind(final Booking booking) {

		boolean showCreate;

		Dataset dataset;
		dataset = super.unbindObject(booking, "travelClass", "price", "locatorCode", "flight");
		showCreate = super.getRequest().getPrincipal().hasRealm(booking.getCustomer());

		super.getResponse().addGlobal("showCreate", showCreate);
		super.getResponse().addData(dataset);
	}
}
