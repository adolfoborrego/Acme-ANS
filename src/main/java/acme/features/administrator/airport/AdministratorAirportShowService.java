
package acme.features.administrator.airport;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airport.Airport;
import acme.entities.airport.AirportOperationalScope;

@GuiService
public class AdministratorAirportShowService extends AbstractGuiService<Administrator, Airport> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorAirportRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;

		status = super.getRequest().getPrincipal().hasRealmOfType(Administrator.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Airport airport;
		int id;

		id = super.getRequest().getData("id", int.class);
		airport = this.repository.findAirportById(id);

		super.getBuffer().addData(airport);
	}

	@Override
	public void unbind(final Airport airport) {
		assert airport != null;
		SelectChoices operationalScopes = SelectChoices.from(AirportOperationalScope.class, airport.getOperationalScope());

		Dataset dataset = super.unbindObject(airport, "name", "iataCode", "city", "country", "operationalScope", "website", "emailAddress", "contactPhoneNumber");
		dataset.put("operationalScopes", operationalScopes);
		super.getResponse().addData(dataset);
	}

}
