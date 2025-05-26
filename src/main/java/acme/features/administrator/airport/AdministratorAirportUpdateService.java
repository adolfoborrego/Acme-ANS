
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

		try {
			if (request.hasData("operationalScope", AirportOperationalScope.class))
				request.getData("operationalScope", AirportOperationalScope.class);
		} catch (Exception e) {
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

		// 1. Obtener objeto original cargado en buffer
		Airport original = this.repository.findAirportById(object.getId());

		// 2. Comprobar si ha cambiado algún atributo
		boolean changed = false;
		if (!object.getName().equals(original.getName()))
			changed = true;
		if (!object.getIataCode().equals(original.getIataCode()))
			changed = true;
		if (object.getOperationalScope() != original.getOperationalScope())
			changed = true;
		if (!object.getCity().equals(original.getCity()))
			changed = true;
		if (!object.getCountry().equals(original.getCountry()))
			changed = true;
		if (object.getWebsite() != null ? !object.getWebsite().equals(original.getWebsite()) : original.getWebsite() != null)
			changed = true;
		if (object.getEmailAddress() != null ? !object.getEmailAddress().equals(original.getEmailAddress()) : original.getEmailAddress() != null)
			changed = true;
		if (object.getContactPhoneNumber() != null ? !object.getContactPhoneNumber().equals(original.getContactPhoneNumber()) : original.getContactPhoneNumber() != null)
			changed = true;

		// 3. Si no hay cambios, lanzamos error global y paramos
		if (!changed) {
			super.state(false, "*", "administrator.airport.form.error.no-changes");
			return;
		}

		// 4. Validación de confirmación
		boolean confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "administrator.airport.form.error.confirmation");

		// 5. Validar unicidad de IATA en Airport
		String iataCode = object.getIataCode();
		Airport existingAirport = this.repository.findAirportByIataCode(iataCode);
		if (existingAirport != null && existingAirport.getId() != object.getId()) {
			super.state(false, "iataCode", "administrator.airport.form.error.duplicate-iata");
			return;
		}

		// 6. Validar unicidad de IATA en Airline
		boolean airlineExists = this.repository.existsAirlineByIataCode(iataCode);
		if (airlineExists)
			super.state(false, "iataCode", "administrator.airport.form.error.duplicate-iata");
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
