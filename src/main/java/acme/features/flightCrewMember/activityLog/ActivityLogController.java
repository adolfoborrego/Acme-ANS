
package acme.features.flightCrewMember.activityLog;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.activityLog.ActivityLog;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiController
public class ActivityLogController extends AbstractGuiController<FlightCrewMember, ActivityLog> {

	//INTERNAL STATE

	@Autowired
	private ActivityLogCreateService	createService;

	@Autowired
	private ActivityLogListService		listService;

	@Autowired
	private ActivityLogPublishService	publishService;

	@Autowired
	private ActivityLogShowService		showService;

	@Autowired
	private ActivityLogUpdateService	updateService;

	@Autowired
	private ActivityLogDeleteService	deleteService;

	//CONTRUCTORS 


	@PostConstruct
	protected void initialise() {

		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("list", this.listService);
		super.addCustomCommand("create-activityLog", "create", this.createService);
		super.addBasicCommand("update", this.updateService);
		super.addBasicCommand("delete", this.deleteService);

		super.addCustomCommand("publish", "update", this.publishService);

	}
}
