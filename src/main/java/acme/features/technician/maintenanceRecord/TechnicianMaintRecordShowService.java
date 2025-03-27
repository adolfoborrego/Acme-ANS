
package acme.features.technician.maintenanceRecord;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.MaintenanceRecord;
import acme.realms.Technician;

@GuiService
public class TechnicianMaintRecordShowService extends AbstractGuiService<Technician, MaintenanceRecord> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private TechnicianMaintRecordRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		// TO IMPLEMENT DETERMINA SI LA REQUEST ES BUENA O NO
	}

	@Override
	public void load() {
		//TO DO PROCESA LOS DATOS NECESARIOS PARA CUMPLIR CON LA REQUEST Y LOS ALMACENA EN EL BUFFER

		super.getBuffer().addData(null);
	}

	@Override
	public void unbind(final MaintenanceRecord maintenanceRecord) {

		// TRANSFORMA LOS DATOS EN LA RESPONSE
		assert maintenanceRecord != null;

		Dataset dataset;
		dataset = super.unbindObject(null); // flight, "tag", "indicator", "cost", "description" el bjeto con los campos que quiero mandar

		super.getResponse().addData(dataset);
	}
}
