
package acme.features.technician.maintenanceRecord;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.MaintenanceRecord;
import acme.realms.Technician;

@GuiController
public class TechnicianMaintenanceRecordController extends AbstractGuiController<Technician, MaintenanceRecord> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private TechnicianMaintRecordListService	listService;

	@Autowired
	private TechnicianMaintRecordShowService	showService;

	// Constructors -----------------------------------------------------------


	@PostConstruct
	protected void initialise() {
		System.out.println("He llegao aqui");
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
	}

}
