
package acme.features.technician.maintenanceRecord;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.Task;
import acme.entities.maintenanceRecord.MaintenanceRecord;
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

		status = super.getRequest().getPrincipal().hasRealmOfType(Technician.class) && mr != null && !mr.getPublished();

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
		int id = super.getRequest().getData("id", int.class);
		Collection<Task> tasks = this.repository.findAllTaskByMaintenanceRecordId(id);
		boolean allPublished = this.allTasksPublished(tasks);
		boolean noTasks = tasks.isEmpty();

		if (noTasks)
			super.state(noTasks, "*", "technician.maintenanceRecord.publish.no-tasks"); // pensar como hacer para que cdo muestre esto desps se vaya a la pantalla show normal
		else
			super.state(allPublished, "*", "technician.maintenanceRecord.publish.task-unpublished");
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
