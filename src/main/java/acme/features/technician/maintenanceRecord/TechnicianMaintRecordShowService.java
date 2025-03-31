
package acme.features.technician.maintenanceRecord;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.maintenanceRecord.MaintenanceRecord;
import acme.entities.maintenanceRecord.MaintenanceRecordStatus;
import acme.realms.technician.Technician;

@GuiService
public class TechnicianMaintRecordShowService extends AbstractGuiService<Technician, MaintenanceRecord> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private TechnicianMaintRecordRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int maintenanceRecordId;
		int userId;
		int technicianId;
		MaintenanceRecord maintenanceRecord;

		userId = super.getRequest().getPrincipal().getAccountId();
		technicianId = this.repository.findTechnicianIdByUserId(userId);
		maintenanceRecordId = super.getRequest().getData("id", int.class);
		maintenanceRecord = this.repository.findById(maintenanceRecordId);

		status = maintenanceRecord != null && technicianId == maintenanceRecord.getTechnician().getId();
		super.getResponse().setAuthorised(status);

	}

	@Override
	public void load() {
		int maintenanceRecordId;
		MaintenanceRecord maintenanceRecord;

		maintenanceRecordId = super.getRequest().getData("id", int.class);
		maintenanceRecord = this.repository.findById(maintenanceRecordId);

		super.getBuffer().addData(maintenanceRecord);
	}

	@Override
	public void unbind(final MaintenanceRecord maintenanceRecord) {

		assert maintenanceRecord != null;

		SelectChoices aircrafts = SelectChoices.from(this.repository.findAllAircraft(), "registrationNumber", maintenanceRecord.getAircraft());
		SelectChoices statuses = SelectChoices.from(MaintenanceRecordStatus.class, maintenanceRecord.getCurrentStatus());
		int numberOfTasks = this.repository.cuentaNumeroTasks(maintenanceRecord.getId());
		Dataset dataset;
		dataset = super.unbindObject(maintenanceRecord, "moment", "currentStatus", "inspectionDueDate", "estimatedCost", "notes", "published", "aircraft");
		dataset.put("aircrafts", aircrafts);
		dataset.put("statusChoices", statuses);

		super.getResponse().addGlobal("numberOfTasks", numberOfTasks);
		super.getResponse().addData(dataset);
	}
}
