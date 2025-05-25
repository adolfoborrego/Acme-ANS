
package acme.features.technician.task;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.task.Task;
import acme.entities.task.TaskType;
import acme.realms.technician.Technician;

@GuiService
public class TechnicianTaskPublishService extends AbstractGuiService<Technician, Task> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private TechnicianTaskRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int userId;
		int technicianId;
		boolean isOwner = false;

		int taskId = super.getRequest().getData("id", int.class, null);
		Task task = this.repository.findById(taskId);
		userId = super.getRequest().getPrincipal().getAccountId();
		technicianId = this.repository.findTechnicianIdByUserId(userId);
		if (task != null)
			isOwner = task.getMaintenanceRecord().getTechnician().getId() == technicianId;

		status = super.getRequest().getPrincipal().hasRealmOfType(Technician.class) && isOwner && !task.getPublished();
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int taskId = super.getRequest().getData("id", int.class);
		Task task = this.repository.findById(taskId);

		super.getBuffer().addData(task);
	}
	@Override
	public void bind(final Task task) {
		assert task != null;
		super.bindObject(task, "type", "description", "priority", "estimatedDuration");
	}

	@Override
	public void validate(final Task task) {

	}

	@Override
	public void perform(final Task task) {
		assert task != null;
		Task toPublish = this.repository.findById(task.getId());
		toPublish.setPublished(true);
		this.repository.save(toPublish);
	}

	@Override
	public void unbind(final Task task) {
		assert task != null;
		SelectChoices types = SelectChoices.from(TaskType.class, task.getType());

		Dataset dataset = super.unbindObject(task, "type", "description", "priority", "estimatedDuration", "published");
		dataset.put("types", types);
		super.getResponse().addGlobal("id", task.getId());
		super.getResponse().addGlobal("maintenanceRecordId", task.getMaintenanceRecord().getId());
		super.getResponse().addGlobal("redirect", false);
		super.getResponse().addData(dataset);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equalsIgnoreCase("POST"))
			PrincipalHelper.handleUpdate();
	}

}
