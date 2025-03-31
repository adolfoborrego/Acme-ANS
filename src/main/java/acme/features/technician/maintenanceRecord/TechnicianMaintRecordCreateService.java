
package acme.features.technician.maintenanceRecord;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.maintenanceRecord.MaintenanceRecord;
import acme.entities.maintenanceRecord.MaintenanceRecordStatus;
import acme.realms.technician.Technician;

@GuiService
public class TechnicianMaintRecordCreateService extends AbstractGuiService<Technician, MaintenanceRecord> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private TechnicianMaintRecordRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;

		status = super.getRequest().getPrincipal().hasRealmOfType(Technician.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		MaintenanceRecord mr = new MaintenanceRecord();
		mr.setPublished(false);
		super.getBuffer().addData(mr);
	}

	@Override
	public void bind(final MaintenanceRecord maintenanceRecord) {
		assert maintenanceRecord != null;
		super.bindObject(maintenanceRecord, "moment", "currentStatus", "inspectionDueDate", "estimatedCost", "notes", "published", "aircraft");
	}

	@Override
	public void validate(final MaintenanceRecord maintenanceRecord) {
		assert maintenanceRecord != null;

		boolean primeroMoment = maintenanceRecord.getMoment().before(maintenanceRecord.getInspectionDueDate());
		super.state(primeroMoment, "moment", "technician.maintenanceRecord.moment-before-inspection.moment");
		super.state(primeroMoment, "inspectionDueDate", "technician.maintenanceRecord.moment-before-inspection.inspectionDueDate");
	}

	@Override
	public void perform(final MaintenanceRecord maintenanceRecord) {
		assert maintenanceRecord != null;
		int userAccountId = super.getRequest().getPrincipal().getAccountId();
		Technician technician = this.repository.findTechnicianByUserId(userAccountId);

		maintenanceRecord.setTechnician(technician);
		this.repository.save(maintenanceRecord);
	}

	@Override
	public void unbind(final MaintenanceRecord maintenanceRecord) {
		SelectChoices aircrafts = SelectChoices.from(this.repository.findAllAircraft(), "registrationNumber", maintenanceRecord.getAircraft());
		SelectChoices statuses = SelectChoices.from(MaintenanceRecordStatus.class, maintenanceRecord.getCurrentStatus());

		Dataset dataset = super.unbindObject(maintenanceRecord, "moment", "currentStatus", "inspectionDueDate", "estimatedCost", "notes", "published", "aircraft");
		dataset.put("aircrafts", aircrafts);
		dataset.put("statusChoices", statuses);
		super.getResponse().addData(dataset);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equalsIgnoreCase("POST"))
			PrincipalHelper.handleUpdate();
	}

}
