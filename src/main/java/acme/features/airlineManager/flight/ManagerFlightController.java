
package acme.features.airlineManager.flight;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.flight.Flight;
import acme.realms.airlineManager.AirlineManager;

@GuiController
public class ManagerFlightController extends AbstractGuiController<AirlineManager, Flight> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ManagerFlightListService	listService;

	@Autowired
	private ManagerFlightShowService	showService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
	}

}
