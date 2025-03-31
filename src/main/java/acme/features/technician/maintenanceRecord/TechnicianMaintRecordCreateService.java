
package acme.features.technician.maintenanceRecord;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircraft.Aircraft;
import acme.entities.maintenanceRecord.MaintenanceRecord;
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
		super.bindObject(maintenanceRecord, "moment", "currentStatus", "inspectionDueDate", "estimatedCost", "notes", "published");
		super.getResponse().addGlobal("aircraft", String.class);
	}

	@Override
	public void validate(final MaintenanceRecord maintenanceRecord) {
		String registrationNumber = super.getRequest().getData("aircraft", String.class);
		Aircraft aircraft = this.repository.findAircraftByRegistrationNumber(registrationNumber);

		if (maintenanceRecord == null)
			throw new IllegalArgumentException();
		if (!this.isValid(maintenanceRecord))
			throw new IllegalArgumentException("No es válida");
		if (aircraft == null)
			throw new IllegalArgumentException("Es necesario un aircraft válido"); // Haciendolo asi muestra un alert no queremos eso queremos que en le form lo corrija preguntar como
	}

	@Override
	public void perform(final MaintenanceRecord maintenanceRecord) {
		assert maintenanceRecord != null;
		int userAccountId = super.getRequest().getPrincipal().getAccountId();
		Technician technician = this.repository.findTechnicianByUserId(userAccountId);
		String registrationNumber = super.getRequest().getData("aircraft", String.class);
		Aircraft aircraft = this.repository.findAircraftByRegistrationNumber(registrationNumber);

		maintenanceRecord.setTechnician(technician);
		maintenanceRecord.setCurrentStatus("PENDING");
		maintenanceRecord.setAircraft(aircraft);

		this.repository.save(maintenanceRecord);
	}

	@Override
	public void unbind(final MaintenanceRecord maintenanceRecord) {
		Dataset dataset = super.unbindObject(maintenanceRecord, "moment", "currentStatus", "inspectionDueDate", "estimatedCost", "notes", "published", "aircraft");
		super.getResponse().addData(dataset);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equalsIgnoreCase("POST"))
			PrincipalHelper.handleUpdate();
	}

	private boolean isValid(final MaintenanceRecord maintenanceRecord) {
		return maintenanceRecord.getMoment().before(maintenanceRecord.getInspectionDueDate());

	}
}
