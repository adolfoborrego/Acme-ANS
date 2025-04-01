
package acme.features.technician.task;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.task.Task;
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
	}

	@Override
	public void validate(final Task task) {

	}

	@Override
	public void perform(final Task task) {
		assert task != null;
		task.setPublished(true);
		this.repository.save(task);
	}

	@Override
	public void unbind(final Task task) {
		super.getResponse().addData(super.unbindObject(task, "published"));

	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equalsIgnoreCase("POST"))
			PrincipalHelper.handleUpdate();
	}

}
