
package acme.features.technician.maintenanceRecord;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.maintenanceRecord.MaintenanceRecord;
import acme.realms.airlineManager.AirlineManager;
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

		status = super.getRequest().getPrincipal().hasRealmOfType(AirlineManager.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		MaintenanceRecord object = new MaintenanceRecord();
		object.setPublished(false);
		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final MaintenanceRecord object) {
		assert object != null;
		super.bindObject(object, "tag", "indicator", "cost", "description");
	}

	@Override
	public void validate(final MaintenanceRecord object) {
		assert object != null;
		// Puedes dejarlo vacío por ahora
	}

	@Override
	public void perform(final MaintenanceRecord object) {
		assert object != null;
		// Hay que enlazarlo altecnico pq esque sino no puedo crear una nueva maintenanceRecord sin tasks ya 
		//que el tecnico lo indica la propia task y eso no puede ser el tecnico tiene q ir ligado a la MR y las tasks igual
		int userAccountId = super.getRequest().getPrincipal().getAccountId();
		//int managerId = this.repository.findTechnicianIdByUserId(userAccountId);

		// Chequear el moment

		//object.setTecnician(tecnician);
		this.repository.save(object);
	}

	@Override
	public void unbind(final MaintenanceRecord object) {
		Dataset dataset = super.unbindObject(object, "tag", "indicator", "cost", "description");
		super.getResponse().addData(dataset);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equalsIgnoreCase("POST"))
			PrincipalHelper.handleUpdate();
	}
}
