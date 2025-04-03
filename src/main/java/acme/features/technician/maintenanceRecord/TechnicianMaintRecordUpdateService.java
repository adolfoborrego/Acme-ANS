
package acme.features.technician.maintenanceRecord;

import java.math.BigDecimal;
import java.util.Objects;

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
public class TechnicianMaintRecordUpdateService extends AbstractGuiService<Technician, MaintenanceRecord> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private TechnicianMaintRecordRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int id = super.getRequest().getData("id", int.class);
		MaintenanceRecord mr = this.repository.findById(id);
		status = super.getRequest().getPrincipal().hasRealmOfType(Technician.class) && mr != null;

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
		assert maintenanceRecord != null;
		super.bindObject(maintenanceRecord, "moment", "currentStatus", "inspectionDueDate", "estimatedCost", "notes", "published", "aircraft");
	}

	@Override
	public void validate(final MaintenanceRecord maintenanceRecord) {
		assert maintenanceRecord != null;
		MaintenanceRecord original = this.repository.findById(maintenanceRecord.getId());

		if (maintenanceRecord.getMoment() != null && maintenanceRecord.getInspectionDueDate() != null) {
			boolean primeroMoment = maintenanceRecord.getMoment().before(maintenanceRecord.getInspectionDueDate());
			super.state(primeroMoment, "moment", "technician.maintenanceRecord.moment-before-inspection.moment");
			super.state(primeroMoment, "inspectionDueDate", "technician.maintenanceRecord.moment-before-inspection.inspectionDueDate");
		}
		boolean hasChanged = this.hasChanged(original, maintenanceRecord);
		super.state(hasChanged, "*", "technician.maintenanceRecord.noChanges");

	}

	@Override
	public void perform(final MaintenanceRecord maintenanceRecord) {
		assert maintenanceRecord != null;
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

	private boolean hasChanged(final MaintenanceRecord original, final MaintenanceRecord nueva) {

		boolean mismoRegNumber = Objects.equals(original.getAircraft().getRegistrationNumber(), nueva.getAircraft().getRegistrationNumber());
		boolean mismasNotas = Objects.equals(original.getNotes(), nueva.getNotes());
		boolean mismoEstado = Objects.equals(original.getCurrentStatus(), nueva.getCurrentStatus());
		boolean mismoCosto = Objects.equals(BigDecimal.valueOf(original.getEstimatedCost().getAmount()).stripTrailingZeros(), nueva.getEstimatedCost() != null ? BigDecimal.valueOf(nueva.getEstimatedCost().getAmount()).stripTrailingZeros() : null);
		boolean mismaFechaInspeccion = Objects.equals(original.getInspectionDueDate().getTime() / 1000, nueva.getInspectionDueDate() != null ? nueva.getInspectionDueDate().getTime() / 1000 : null);
		boolean mismoMomento = Objects.equals(original.getMoment().getTime() / 1000, nueva.getMoment() != null ? nueva.getMoment().getTime() / 1000 : null);

		return !mismoRegNumber || !mismasNotas || !mismoEstado || !mismoCosto || !mismaFechaInspeccion || !mismoMomento;
	}

}
