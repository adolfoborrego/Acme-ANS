
package acme.features.airlineManager.leg;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.leg.Leg;
import acme.realms.airlineManager.AirlineManager;

@GuiController
public class ManagerLegController extends AbstractGuiController<AirlineManager, Leg> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ManagerLegListService	listService;

	@Autowired
	private ManagerLegShowService	showService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
	}
}
