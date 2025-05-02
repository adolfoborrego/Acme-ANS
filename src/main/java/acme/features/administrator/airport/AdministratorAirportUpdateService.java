
package acme.features.administrator.airport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airport.Airport;
import acme.entities.airport.AirportOperationalScope;

@Service
@GuiService
public class AdministratorAirportUpdateService extends AbstractGuiService<Administrator, Airport> {

	@Autowired
	protected AdministratorAirportRepository repository;


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
		Airport airline = this.repository.findAirportById(id);

		// Must exist
		boolean authorised = airline != null;
		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		Airport airport = this.repository.findAirportById(id);
		super.getBuffer().addData(airport);
	}

	@Override
	public void bind(final Airport object) {
		assert object != null;
		super.bindObject(object, "name", "iataCode", "operationalScope", "city", "country", "website", "emailAddress", "contactPhoneNumber");
	}

	@Override
	public void validate(final Airport object) {
		assert object != null;
		boolean confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "administrator.airport.form.error.confirmation");
	}

	@Override
	public void perform(final Airport object) {
		assert object != null;
		this.repository.save(object);
	}

	@Override
	public void unbind(final Airport object) {
		assert object != null;
		SelectChoices operationalScopes = SelectChoices.from(AirportOperationalScope.class, object.getOperationalScope());

		Dataset dataset = super.unbindObject(object, "name", "iataCode", "operationalScope", "city", "country", "website", "emailAddress", "contactPhoneNumber");
		dataset.put("confirmation", false);
		dataset.put("operationalScopes", operationalScopes);

		super.getResponse().addData(dataset);
	}
}
