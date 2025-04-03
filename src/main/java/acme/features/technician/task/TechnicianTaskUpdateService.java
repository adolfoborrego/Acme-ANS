
package acme.features.technician.task;

import java.util.Objects;

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
public class TechnicianTaskUpdateService extends AbstractGuiService<Technician, Task> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private TechnicianTaskRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int userId;
		int technicianId;

		int taskId = super.getRequest().getData("id", int.class);
		Task task = this.repository.findById(taskId);
		userId = super.getRequest().getPrincipal().getAccountId();
		technicianId = this.repository.findTechnicianIdByUserId(userId);
		Technician technicianOfTask = this.repository.findTechnicianByTaskId(taskId);

		status = super.getRequest().getPrincipal().hasRealmOfType(Technician.class) && technicianId == technicianOfTask.getId() && !task.getPublished();
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
		super.bindObject(task, "type", "description", "priority", "estimatedDuration", "published");
	}

	@Override
	public void validate(final Task task) {
		assert task != null;
		Task original = this.repository.findById(task.getId());
		boolean haCambiado = this.hasChanged(original, task);
		super.state(haCambiado, "*", "technician.task.update.no-changes");
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

	private boolean hasChanged(final Task original, final Task nueva) {
		boolean mismaDescription = Objects.equals(nueva.getDescription(), original.getDescription());
		boolean mismaPriority = Objects.equals(nueva.getPriority(), original.getPriority());
		boolean mismaEstimatedDuration = Objects.equals(nueva.getEstimatedDuration(), original.getEstimatedDuration());
		boolean mismoType = Objects.equals(nueva.getType().toString(), original.getType().toString());

		return !mismaDescription || !mismaPriority || !mismaEstimatedDuration || !mismoType;
	}

}
