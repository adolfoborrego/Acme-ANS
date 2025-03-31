
package acme.features.technician.maintenanceRecord;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.maintenanceRecord.MaintenanceRecord;
import acme.realms.technician.Technician;

@GuiController
public class TechnicianMaintenanceRecordController extends AbstractGuiController<Technician, MaintenanceRecord> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private TechnicianMaintRecordListService	listService;

	@Autowired
	private TechnicianMaintRecordShowService	showService;

	@Autowired
	private TechnicianMaintRecordCreateService	createService;

	@Autowired
	private TechnicianMaintRecordUpdateService	updateService;

	@Autowired
	private TechnicianMaintRecordPublishService	publishService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("update", this.updateService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
		super.addCustomCommand("publish", "update", this.publishService);
	}

}
