
package acme.features.technician.task;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.task.Task;
import acme.entities.task.TaskType;
import acme.realms.technician.Technician;

@GuiService
public class TechnicianTaskShowService extends AbstractGuiService<Technician, Task> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private TechnicianTaskRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int taskId;
		int userId;
		int technicianId;
		Task task;

		userId = super.getRequest().getPrincipal().getAccountId();
		technicianId = this.repository.findTechnicianIdByUserId(userId);
		taskId = super.getRequest().getData("id", int.class);
		task = this.repository.findById(taskId);

		status = task != null && technicianId == task.getMaintenanceRecord().getTechnician().getId();
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Task data;
		int taskId;

		taskId = super.getRequest().getData("id", int.class);
		data = this.repository.findById(taskId);

		super.getBuffer().addData(data);
	}

	@Override
	public void unbind(final Task task) {

		assert task != null;

		Dataset dataset;
		SelectChoices types = SelectChoices.from(TaskType.class, task.getType());
		dataset = super.unbindObject(task, "type", "description", "priority", "estimatedDuration", "published");
		dataset.put("types", types);
		super.getResponse().addData(dataset);
	}
}
