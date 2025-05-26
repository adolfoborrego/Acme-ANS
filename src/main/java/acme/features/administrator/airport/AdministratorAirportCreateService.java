
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
public class AdministratorAirportCreateService extends AbstractGuiService<Administrator, Airport> {

	@Autowired
	protected AdministratorAirportRepository repository;


	@Override
	public void authorise() {

		var request = super.getRequest();
		var principal = request.getPrincipal();
		if (request.hasData("id", int.class) && request.getData("id", int.class) != 0) {
			super.getResponse().setAuthorised(false);
		return;}

		super.getResponse().setAuthorised(super.getRequest().getPrincipal().hasRealmOfType(Administrator.class));
		try {
			if (request.hasData("operationalScope", AirportOperationalScope.class))
				request.getData("operationalScope", AirportOperationalScope.class);
		} catch (Exception e) {
			super.getResponse().setAuthorised(false);
			return;
		}

	}

	@Override
	public void load() {
		Airport airport = new Airport();
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

		String iataCode = object.getIataCode();
		// Validar que ningún otro aeropuerto tenga el mismo IATA
		Airport existingAirport = this.repository.findAirportByIataCode(iataCode);
		if (existingAirport != null && existingAirport.getId() != object.getId()) {
			super.state(false, "iataCode", "administrator.airport.form.error.duplicate-iata");
			return;
		}

		// Validar que ninguna aerolínea tenga ese mismo IATA
		boolean airlineExists = this.repository.existsAirlineByIataCode(iataCode);
		if (airlineExists) {
			super.state(false, "iataCode", "administrator.airport.form.error.duplicate-iata");
			return;
		}
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
