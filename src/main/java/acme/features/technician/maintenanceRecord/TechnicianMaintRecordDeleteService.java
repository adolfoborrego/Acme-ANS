
package acme.features.technician.maintenanceRecord;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircraft.AircraftStatus;
import acme.entities.maintenanceRecord.MaintenanceRecord;
import acme.entities.maintenanceRecord.MaintenanceRecordStatus;
import acme.entities.task.Task;
import acme.features.technician.task.TechnicianTaskRepository;
import acme.realms.technician.Technician;

@GuiService
public class TechnicianMaintRecordDeleteService extends AbstractGuiService<Technician, MaintenanceRecord> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private TechnicianMaintRecordRepository	repository;

	@Autowired
	private TechnicianTaskRepository		taskRepository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int id = super.getRequest().getData("id", int.class, null);
		MaintenanceRecord mr = this.repository.findById(id);
		int userId = super.getRequest().getPrincipal().getAccountId();
		Technician technicianRequest = this.repository.findTechnicianByUserId(userId);

		status = super.getRequest().getPrincipal().hasRealmOfType(Technician.class) && mr != null && technicianRequest.getId() == mr.getTechnician().getId() && !mr.getPublished();
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		MaintenanceRecord mr = this.repository.findById(id);
		super.getBuffer().addData(mr);
	}

	@Override
	public void bind(final MaintenanceRecord maintenanceRecord) {
	}

	@Override
	public void validate(final MaintenanceRecord maintenanceRecord) {
	}

	@Override
	public void perform(final MaintenanceRecord maintenanceRecord) {
		assert maintenanceRecord != null;
		Collection<Task> allTasks = this.repository.findAllTaskByMaintenanceRecordId(maintenanceRecord.getId());
		for (Task t : allTasks)
			this.taskRepository.delete(t);

		this.repository.delete(maintenanceRecord);
	}

	@Override
	public void unbind(final MaintenanceRecord maintenanceRecord) {
		SelectChoices statuses = SelectChoices.from(MaintenanceRecordStatus.class, maintenanceRecord.getCurrentStatus());
		int numberOfTasks = this.repository.cuentaNumeroTasks(maintenanceRecord.getId());
		Collection<Task> tasks = this.repository.findAllTaskByMaintenanceRecordId(maintenanceRecord.getId());
		boolean allTasksPublished = this.allTasksPublished(tasks);
		boolean isAircraftDisabled = maintenanceRecord.getAircraft().getStatus().equals(AircraftStatus.DISABLED);

		Dataset dataset = super.unbindObject(maintenanceRecord, "moment", "currentStatus", "inspectionDueDate", "estimatedCost", "notes", "published");
		dataset.put("aircraft", maintenanceRecord.getAircraft().getRegistrationNumber());
		super.getResponse().addGlobal("maintenanceRecordId", maintenanceRecord.getId());
		super.getResponse().addGlobal("allTasksPublished", allTasksPublished);
		super.getResponse().addGlobal("isAircraftDisabled", isAircraftDisabled);
		dataset.put("statusChoices", statuses);
		super.getResponse().addGlobal("numberOfTasks", numberOfTasks);
		super.getResponse().addGlobal("redirect", false);
		super.getResponse().addData(dataset);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equalsIgnoreCase("POST"))
			PrincipalHelper.handleUpdate();
	}

	private boolean allTasksPublished(final Collection<Task> tasks) {
		return tasks.stream().allMatch(Task::getPublished);
	}
}
