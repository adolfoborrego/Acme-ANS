
package acme.features.technician.maintenanceRecord;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.maintenanceRecord.MaintenanceRecord;
import acme.entities.task.Task;
import acme.realms.technician.Technician;

@GuiService
public class TechnicianMaintRecordPublishService extends AbstractGuiService<Technician, MaintenanceRecord> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private TechnicianMaintRecordRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int id = super.getRequest().getData("id", int.class);
		MaintenanceRecord mr = this.repository.findById(id);
		Collection<Task> tasks = this.repository.findAllTaskByMaintenanceRecordId(id);
		boolean allPublished = this.allTasksPublished(tasks);
		int userId = super.getRequest().getPrincipal().getAccountId();
		Technician technicianRequest = this.repository.findTechnicianByUserId(userId);

		status = super.getRequest().getPrincipal().hasRealmOfType(Technician.class) && mr != null && technicianRequest.getId() == mr.getTechnician().getId() && !mr.getPublished() && !tasks.isEmpty() && allPublished;

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
		maintenanceRecord.setPublished(true);
		this.repository.save(maintenanceRecord);
	}

	@Override
	public void unbind(final MaintenanceRecord maintenanceRecord) {
		super.getResponse().addData(super.unbindObject(maintenanceRecord, "published"));
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
