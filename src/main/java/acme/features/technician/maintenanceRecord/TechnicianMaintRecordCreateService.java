
package acme.features.technician.maintenanceRecord;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircraft.Aircraft;
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
		boolean isAircraftAvailable = true;

		Integer aircraftId = super.getRequest().getData("aircraft", int.class, null);
		if (aircraftId != null && aircraftId != 0) {
			Collection<Aircraft> availableAircrafts = this.repository.findAllAircraft();
			isAircraftAvailable = availableAircrafts.stream().anyMatch(a -> a.getId() == aircraftId);
		}

		boolean status = super.getRequest().getPrincipal().hasRealmOfType(Technician.class) && isAircraftAvailable;
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		MaintenanceRecord mr = new MaintenanceRecord();
		mr.setPublished(false);
		mr.setCurrentStatus(MaintenanceRecordStatus.PENDING);
		mr.setMoment(MomentHelper.getCurrentMoment());
		super.getBuffer().addData(mr);
	}

	@Override
	public void bind(final MaintenanceRecord maintenanceRecord) {
		assert maintenanceRecord != null;
		super.bindObject(maintenanceRecord, "inspectionDueDate", "estimatedCost", "notes", "aircraft");
	}

	@Override
	public void validate(final MaintenanceRecord maintenanceRecord) {
		assert maintenanceRecord != null;
		boolean dateNoNull = maintenanceRecord.getMoment() == null || maintenanceRecord.getInspectionDueDate() == null;
		super.state(maintenanceRecord.getAircraft() != null, "aircraft", "technician.maintenanceRecord.aircraft-non-null");

		if (maintenanceRecord.getEstimatedCost() != null) {
			boolean moneyValida = MaintenanceRecord.isPrefixValid(maintenanceRecord);
			super.state(moneyValida, "estimatedCost", "technician.maintenanceRecord.estimatedCost-prefix-valid");
		}

		if (!dateNoNull) {
			boolean primeroMoment = maintenanceRecord.getMoment().before(maintenanceRecord.getInspectionDueDate());
			super.state(primeroMoment, "inspectionDueDate", "technician.maintenanceRecord.moment-before-inspection.inspectionDueDate");
		}
	}

	@Override
	public void perform(final MaintenanceRecord maintenanceRecord) {
		assert maintenanceRecord != null;
		int userAccountId = super.getRequest().getPrincipal().getAccountId();
		Technician technician = this.repository.findTechnicianByUserId(userAccountId);
		maintenanceRecord.setCurrentStatus(MaintenanceRecordStatus.PENDING);
		maintenanceRecord.setMoment(MomentHelper.getCurrentMoment());
		maintenanceRecord.setTechnician(technician);
		this.repository.save(maintenanceRecord);
	}

	@Override
	public void unbind(final MaintenanceRecord maintenanceRecord) {
		assert maintenanceRecord != null;
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
