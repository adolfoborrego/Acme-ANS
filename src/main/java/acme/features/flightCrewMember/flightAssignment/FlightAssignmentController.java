
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
	private FlightAssignmentListCompletedService			listCompletedService;

	@Autowired
	private FlightAssignmentShowService						showService;

	@Autowired
	private FlightAssignmentListPlannedService				listPlannedService;

	@Autowired
	private FlightAssignmentCreateService					createService;

	@Autowired
	private FlightAssignmentDeleteService					deleteService;

	@Autowired
	private FlightAssignmentUpdateService					updateService;

	@Autowired
	private FlightAssignmentPublishService					publishService;

	@Autowired
	private FlightAssignmentListCrewsService				listCrewsService;

	@Autowired
	private FlightAssignmentAddService						addService;

	@Autowired
	private FlightAssignmentListLeadAttendantCrewsService	listLACrewsService;

	@Autowired
	private FlightAssignmentShowCrewDetailService			showCrewService;

	@Autowired
	private FlightAssignmentDeleteCrewDetailService			deleteCrewService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addCustomCommand("list-completed", "list", this.listCompletedService);
		super.addCustomCommand("list-planned", "list", this.listPlannedService);
		super.addCustomCommand("list-crews", "list", this.listCrewsService);
		super.addCustomCommand("list-LeadAttendantCrews", "list", this.listLACrewsService);

		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
		super.addCustomCommand("addFlightAssignment", "create", this.addService);
		super.addBasicCommand("delete", this.deleteService);
		super.addBasicCommand("update", this.updateService);
		super.addCustomCommand("publish", "update", this.publishService);
		super.addCustomCommand("showCrewDetail", "show", this.showCrewService);
		super.addCustomCommand("deleteCrewDetail", "delete", this.deleteCrewService);

	}

}
