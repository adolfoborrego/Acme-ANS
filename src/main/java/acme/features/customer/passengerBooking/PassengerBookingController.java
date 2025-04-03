
package acme.features.customer.passengerBooking;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.PassengerBooking;
import acme.realms.Customer;

@GuiController
public class PassengerBookingController extends AbstractGuiController<Customer, PassengerBooking> {

	@Autowired
	private PassengerBookingListService		listService;

	@Autowired
	private PassengerBookingCreateService	createService;

	@Autowired
	private PassengerBookingShowService		showService;


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("show", this.showService);

	}

}
