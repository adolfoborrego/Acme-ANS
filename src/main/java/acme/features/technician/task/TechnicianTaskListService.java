
package acme.features.technician.task;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.maintenanceRecord.MaintenanceRecord;
import acme.entities.task.Task;
import acme.realms.technician.Technician;

@GuiService
public class TechnicianTaskListService extends AbstractGuiService<Technician, Task> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private TechnicianTaskRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int maintenanceRecordId;
		int userId;
		int technicianId;
		MaintenanceRecord mr;

		maintenanceRecordId = super.getRequest().getData("maintenanceRecordId", int.class);
		userId = super.getRequest().getPrincipal().getAccountId();
		technicianId = this.repository.findTechnicianIdByUserId(userId);
		mr = this.repository.findMaintenanceRecordById(maintenanceRecordId);

		status = super.getRequest().getPrincipal().hasRealmOfType(Technician.class) && technicianId == mr.getTechnician().getId();
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Collection<Task> data;
		int maintenanceRecordId;

		maintenanceRecordId = super.getRequest().getData("maintenanceRecordId", int.class);
		data = this.repository.findTaskByMaintenanceRecordId(maintenanceRecordId);

		super.getBuffer().addData(data);
	}

	@Override
	public void unbind(final Task task) {

		assert task != null;

		Dataset dataset;
		int maintenanceRecordId = super.getRequest().getData("maintenanceRecordId", int.class);
		MaintenanceRecord maintenanceRecord = this.repository.findMaintenanceRecordById(maintenanceRecordId);
		boolean showCreate = !maintenanceRecord.getPublished();
		dataset = super.unbindObject(task, "id", "type", "priority", "estimatedDuration", "published");
		super.getResponse().addGlobal("showCreate", showCreate);
		super.getResponse().addGlobal("maintenanceRecordId", maintenanceRecordId);

		super.getResponse().addData(dataset);
	}
}
