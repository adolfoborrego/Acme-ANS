
package acme.features.customer.passengerBooking;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.PassengerBooking;
import acme.realms.Customer;

@GuiService
public class PassengerBookingListService extends AbstractGuiService<Customer, PassengerBooking> {

	@Autowired
	private PassengerBookingRepository passengerBookingRepository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		status = super.getRequest().getPrincipal().hasRealmOfType(Customer.class);
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {

		Collection<PassengerBooking> data;
		data = this.passengerBookingRepository.findAllPassengerBooking();

		super.getBuffer().addData(data);
	}

	@Override
	public void unbind(final PassengerBooking passengerBooking) {

		assert passengerBooking != null;

		Dataset dataset;
		dataset = super.unbindObject(passengerBooking, "booking", "passenger");

		super.getResponse().addData(dataset);

	}

}
