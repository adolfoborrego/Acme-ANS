
package acme.features.technician.maintenanceRecord;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.maintenanceRecord.MaintenanceRecord;
import acme.realms.technician.Technician;

@GuiService
public class TechnicianMaintRecordListService extends AbstractGuiService<Technician, MaintenanceRecord> {

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
		Collection<MaintenanceRecord> data;
		int userId;
		Technician technician;

		userId = super.getRequest().getPrincipal().getAccountId();
		technician = this.repository.findTechnicianByUserId(userId);
		data = technician.getMaintenanceRecords();

		super.getBuffer().addData(data);
	}

	@Override
	public void unbind(final MaintenanceRecord maintenanceRecord) {

		assert maintenanceRecord != null;

		Dataset dataset;
		dataset = super.unbindObject(maintenanceRecord, "currentStatus", "estimatedCost", "inspectionDueDate", "notes", "published");
		dataset.put("aircraft", maintenanceRecord.getAircraft().getRegistrationNumber());

		super.getResponse().addData(dataset);
	}
}
