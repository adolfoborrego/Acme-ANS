
package acme.features.airlineManager.flight;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flight.Flight;
import acme.realms.airlineManager.AirlineManager;

@GuiService
public class ManagerFlightCreateService extends AbstractGuiService<AirlineManager, Flight> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ManagerFlightRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;

		status = super.getRequest().getPrincipal().hasRealmOfType(AirlineManager.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Flight object = new Flight();
		object.setPublished(false);
		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final Flight object) {
		assert object != null;
		super.bindObject(object, "tag", "indicator", "cost", "description");
	}

	@Override
	public void validate(final Flight object) {
		assert object != null;
		// Puedes dejarlo vacío por ahora
	}

	@Override
	public void perform(final Flight object) {
		assert object != null;

		int userAccountId = super.getRequest().getPrincipal().getAccountId();
		int managerId = this.repository.findManagerByUsserAccountId(userAccountId);

		AirlineManager manager = new AirlineManager();
		manager.setId(managerId);

		object.setAirlineManager(manager);
		this.repository.save(object);
	}

	@Override
	public void unbind(final Flight object) {
		Dataset dataset = super.unbindObject(object, "tag", "indicator", "cost", "description");
		super.getResponse().addData(dataset);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equalsIgnoreCase("POST"))
			PrincipalHelper.handleUpdate();
	}
}
