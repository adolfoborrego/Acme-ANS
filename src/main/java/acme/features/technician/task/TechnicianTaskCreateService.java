
package acme.features.technician.task;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircraft.AircraftStatus;
import acme.entities.maintenanceRecord.MaintenanceRecord;
import acme.entities.task.Task;
import acme.entities.task.TaskType;
import acme.realms.technician.Technician;

@GuiService
public class TechnicianTaskCreateService extends AbstractGuiService<Technician, Task> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private TechnicianTaskRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int maintenanceRecordId = super.getRequest().getData("maintenanceRecordId", int.class, null);
		MaintenanceRecord maintenanceRecord = this.repository.findMaintenanceRecordById(maintenanceRecordId);
		boolean isAircraftDisabled = false;
		if (maintenanceRecord != null)
			isAircraftDisabled = maintenanceRecord.getAircraft().getStatus().equals(AircraftStatus.DISABLED);

		int userId = super.getRequest().getPrincipal().getAccountId();
		Integer technicianRequestId = this.repository.findTechnicianIdByUserId(userId);

		status = super.getRequest().getPrincipal().hasRealmOfType(Technician.class) && maintenanceRecord != null && !maintenanceRecord.getPublished() && technicianRequestId == maintenanceRecord.getTechnician().getId() && !isAircraftDisabled;

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Task task = new Task();
		int maintenanceRecordId = super.getRequest().getData("maintenanceRecordId", int.class);
		MaintenanceRecord maintenanceRecord = this.repository.findMaintenanceRecordById(maintenanceRecordId);

		task.setMaintenanceRecord(maintenanceRecord);
		task.setPublished(false);
		super.getBuffer().addData(task);
	}

	@Override
	public void bind(final Task task) {
		assert task != null;
		super.bindObject(task, "type", "description", "priority", "estimatedDuration");
	}

	@Override
	public void validate(final Task task) {
		assert task != null;

	}

	@Override
	public void perform(final Task task) {
		assert task != null;
		this.repository.save(task);
	}

	@Override
	public void unbind(final Task task) {
		assert task != null;
		SelectChoices types = SelectChoices.from(TaskType.class, task.getType());

		Dataset dataset = super.unbindObject(task, "type", "description", "priority", "estimatedDuration", "published");
		dataset.put("types", types);
		super.getResponse().addGlobal("maintenanceRecordId", task.getMaintenanceRecord().getId());
		super.getResponse().addData(dataset);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equalsIgnoreCase("POST"))
			PrincipalHelper.handleUpdate();
	}

}
