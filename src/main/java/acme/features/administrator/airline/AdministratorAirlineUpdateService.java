
package acme.features.administrator.airline;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airline.Airline;
import acme.entities.airline.AirlineType;

@GuiService
public class AdministratorAirlineUpdateService extends AbstractGuiService<Administrator, Airline> {

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
		int airlineId = super.getRequest().getData("id", int.class);
		Airline airline = this.repository.findAirlineById(airlineId);

		super.getBuffer().addData(airline);
	}

	@Override
	public void bind(final Airline airline) {
		assert airline != null;
		super.bindObject(airline, "name", "iataCode", "website", "type", "foundationTime", "email", "phoneNumber");
	}

	@Override
	public void validate(final Airline airline) {
		assert airline != null;
		Airline original = this.repository.findAirlineById(airline.getId());
		boolean confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "administrator.airline.form.error.confirmation");

		boolean noRepetidoIataCode = this.noRepetidoIataCode(original, airline);
		super.state(noRepetidoIataCode, "iataCode", "administrator.airline.repeated-iataCode");

		boolean hasChanged = this.hasChanged(original, airline);
		super.state(hasChanged, "*", "administrator.airline.no-changes");
	}

	@Override
	public void perform(final Airline airline) {
		assert airline != null;

		this.repository.save(airline);
	}

	@Override
	public void unbind(final Airline airline) {
		assert airline != null;

		SelectChoices typeChoices = SelectChoices.from(AirlineType.class, airline.getType());

		Dataset dataset = super.unbindObject(airline, "name", "iataCode", "website", "type", "foundationTime", "email", "phoneNumber");
		dataset.put("typeChoices", typeChoices);

		super.getResponse().addData(dataset);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equalsIgnoreCase("POST"))
			PrincipalHelper.handleUpdate();
	}

	private boolean hasChanged(final Airline original, final Airline nuevo) {
		boolean mismoNombre = Objects.equals(nuevo.getName(), original.getName());
		boolean mismoIataCode = Objects.equals(nuevo.getIataCode(), original.getIataCode());
		boolean mismaWebsite = Objects.equals(nuevo.getWebsite(), original.getWebsite());
		boolean mismoType = Objects.equals(nuevo.getType(), original.getType());
		boolean mismoPhoneNumber = Objects.equals(nuevo.getPhoneNumber(), original.getPhoneNumber());
		boolean mismoEmail = Objects.equals(nuevo.getEmail(), original.getEmail());
		boolean mismoFoundationTime = Objects.equals(original.getFoundationTime().getTime() / 1000, nuevo.getFoundationTime() != null ? nuevo.getFoundationTime().getTime() / 1000 : null);

		return !mismoNombre || !mismoIataCode || !mismaWebsite || !mismoType || !mismoPhoneNumber || !mismoEmail || !mismoFoundationTime;
	}

	private boolean noRepetidoIataCode(final Airline original, final Airline airline) {
		String iataCodeOriginal = original.getIataCode();
		String iataCodeNuevo = airline.getIataCode();

		if (iataCodeOriginal.equals(iataCodeNuevo))
			return true;
		else {
			List<String> iataCodes = this.repository.findAllAirlines().stream().map(Airline::getIataCode).toList();
			boolean existeRepetido = iataCodes.stream().anyMatch(x -> airline.getIataCode().equals(x));
			return !existeRepetido;
		}
	}

}
