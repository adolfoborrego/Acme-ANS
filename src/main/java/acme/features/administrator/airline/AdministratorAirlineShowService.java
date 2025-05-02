
package acme.features.administrator.airline;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airline.Airline;
import acme.entities.airline.AirlineType;

@GuiService
public class AdministratorAirlineShowService extends AbstractGuiService<Administrator, Airline> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorAirlineRepository repository;

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
		Airline airline = this.repository.findAirlineById(id);

		// Must exist
		boolean authorised = airline != null;
		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		Airline airline;
		int id;

		id = super.getRequest().getData("id", int.class);
		airline = this.repository.findAirlineById(id);

		super.getBuffer().addData(airline);
	}

	@Override
	public void unbind(final Airline airline) {
		assert airline != null;
		SelectChoices typeChoices = SelectChoices.from(AirlineType.class, airline.getType());

		Dataset dataset = super.unbindObject(airline, "name", "iataCode", "website", "type", "foundationTime", "email", "phoneNumber");
		dataset.put("typeChoices", typeChoices);

		super.getResponse().addData(dataset);
	}
}
