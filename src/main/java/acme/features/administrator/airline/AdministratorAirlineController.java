
package acme.features.administrator.airline;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.principals.Administrator;
import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.airline.Airline;

@GuiController
public class AdministratorAirlineController extends AbstractGuiController<Administrator, Airline> {

	@Autowired
	protected AdministratorAirlineListService	listService;

	@Autowired
	protected AdministratorAirlineShowService	showService;


	@PostConstruct
	public void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
	}
}
