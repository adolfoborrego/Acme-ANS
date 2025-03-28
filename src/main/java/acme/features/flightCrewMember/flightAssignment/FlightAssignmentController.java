
package acme.features.flightCrewMember.flightAssignment;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.flightAssignment.FlightAssignment;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiController
public class FlightAssignmentController extends AbstractGuiController<FlightCrewMember, FlightAssignment> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private FlightAssignmentListCompletedService	listCompletedService;

	@Autowired
	private FlightAssignmentShowService				showService;

	@Autowired
	private FlightAssignmentListPlannedService		listPlannedService;

	@Autowired
	private FlightAssignmentCreateService createService;

	@Autowired
	private FlightAssignmentDeleteService deleteService;

	@Autowired
	private FlightAssignmentUpdateService updateService;

	@Autowired
	private FlightAssignmentPublishService publishService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addCustomCommand("list-completed", "list", this.listCompletedService);
		super.addCustomCommand("list-planned", "list", this.listPlannedService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("delete", this.deleteService);
		super.addBasicCommand("update", this.updateService);
		super.addCustomCommand("publish", this.publishService);


	}

}
