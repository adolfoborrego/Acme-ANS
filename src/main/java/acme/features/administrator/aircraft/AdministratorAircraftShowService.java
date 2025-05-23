
package acme.features.administrator.aircraft;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircraft.Aircraft;
import acme.entities.aircraft.AircraftStatus;

@GuiService
public class AdministratorAircraftShowService extends AbstractGuiService<Administrator, Aircraft> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorAircraftRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		var request = super.getRequest();
		var principal = request.getPrincipal();

		// Must be Administrator
		if (!principal.hasRealmOfType(Administrator.class) || !request.hasData("id", int.class)) {
			super.getResponse().setAuthorised(false);
			return;
		}
		int id = request.getData("id", int.class);
		Aircraft aircraft = this.repository.findById(id);

		// Must exist
		boolean authorised = aircraft != null;
		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		int aircraftId;
		Aircraft aircraft;

		aircraftId = super.getRequest().getData("id", int.class);
		aircraft = this.repository.findById(aircraftId);
		super.getBuffer().addData(aircraft);
	}

	@Override
	public void unbind(final Aircraft aircraft) {
		assert aircraft != null;
		boolean showCreate;
		Dataset dataset;
		SelectChoices statusChoices = SelectChoices.from(AircraftStatus.class, aircraft.getStatus());
		SelectChoices choicesAirline = SelectChoices.from(this.repository.findAllAirlines(), "iataCode", aircraft.getAirline());
		dataset = super.unbindObject(aircraft, "model", "registrationNumber", "capacity", "cargoWeight", "status", "details", "airline");
		dataset.put("choicesAirline", choicesAirline);
		dataset.put("statusChoices", statusChoices);

		showCreate = super.getRequest().getPrincipal().hasRealmOfType(Administrator.class);
		super.getResponse().addGlobal("showCreate", showCreate);
		super.getResponse().addData(dataset);
	}

}
