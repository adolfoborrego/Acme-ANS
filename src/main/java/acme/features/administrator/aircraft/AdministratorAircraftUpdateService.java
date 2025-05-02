
package acme.features.administrator.aircraft;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircraft.Aircraft;
import acme.entities.aircraft.AircraftStatus;

@GuiService
public class AdministratorAircraftUpdateService extends AbstractGuiService<Administrator, Aircraft> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorAircraftRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		var principal = super.getRequest().getPrincipal();
		boolean authorised = principal.hasRealmOfType(Administrator.class);

		// Prevent update if aircraft is DISABLED
		if (authorised && super.getRequest().hasData("id", int.class)) {
			int id = super.getRequest().getData("id", int.class);
			Aircraft aircraft = this.repository.findById(id);
			if (aircraft != null && aircraft.getStatus() == AircraftStatus.DISABLED)
				authorised = false;
		}

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		int aircraftId = super.getRequest().getData("id", int.class);
		Aircraft aircraft = this.repository.findById(aircraftId);

		super.getBuffer().addData(aircraft);
	}

	@Override
	public void bind(final Aircraft aircraft) {
		assert aircraft != null;
		super.bindObject(aircraft, "model", "registrationNumber", "capacity", "cargoWeight", "status", "details", "airline");
	}

	@Override
	public void validate(final Aircraft aircraft) {
		assert aircraft != null;
		Aircraft original = this.repository.findById(aircraft.getId());
		boolean confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "administrator.aircraft.form.error.confirmation");

		boolean noRepetidoRegistrationNumber = this.noRepetidoRegistrationNumber(original, aircraft);
		super.state(noRepetidoRegistrationNumber, "registrationNumber", "administrator.aircraft.repeated-registrationNumber");

		boolean hasChanged = this.hasChanged(original, aircraft);
		super.state(hasChanged, "*", "administrator.aircraft.no-changes");
	}

	@Override
	public void perform(final Aircraft aircraft) {
		assert aircraft != null;
		this.repository.save(aircraft);
	}

	@Override
	public void unbind(final Aircraft aircraft) {
		assert aircraft != null;
		SelectChoices choicesAirline = SelectChoices.from(this.repository.findAllAirlines(), "iataCode", aircraft.getAirline());
		SelectChoices statusChoices = SelectChoices.from(AircraftStatus.class, aircraft.getStatus());

		Dataset dataset = super.unbindObject(aircraft, "model", "registrationNumber", "capacity", "cargoWeight", "status", "details", "airline");
		dataset.put("choicesAirline", choicesAirline);
		dataset.put("statusChoices", statusChoices);

		super.getResponse().addData(dataset);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equalsIgnoreCase("POST"))
			PrincipalHelper.handleUpdate();
	}

	private boolean hasChanged(final Aircraft original, final Aircraft nuevo) {
		boolean mismoModel = Objects.equals(nuevo.getModel(), original.getModel());
		boolean mismoRegistrationNumber = Objects.equals(nuevo.getRegistrationNumber(), original.getRegistrationNumber());
		boolean mismaCapacity = Objects.equals(nuevo.getCapacity(), original.getCapacity());
		boolean mismoCargoWeight = Objects.equals(nuevo.getCargoWeight(), original.getCargoWeight());
		boolean mismoStatus = Objects.equals(nuevo.getStatus().toString(), original.getStatus().toString());
		boolean mismosDetails = Objects.equals(nuevo.getDetails(), original.getDetails());
		boolean mismoAirline = Objects.equals(nuevo.getAirline().getIataCode(), original.getAirline().getIataCode());

		return !mismoModel || !mismoRegistrationNumber || !mismaCapacity || !mismoCargoWeight || !mismoStatus || !mismosDetails || !mismoAirline;
	}

	private boolean noRepetidoRegistrationNumber(final Aircraft original, final Aircraft nuevo) {

		String registrationNumberOriginal = original.getRegistrationNumber();
		String registrationNumberNuevo = nuevo.getRegistrationNumber();

		if (registrationNumberOriginal.equals(registrationNumberNuevo))
			return true;
		else {
			List<String> registrationNumbers = this.repository.findAllAircrafts().stream().map(Aircraft::getRegistrationNumber).toList();
			boolean existeRepetido = registrationNumbers.stream().anyMatch(x -> nuevo.getRegistrationNumber().equals(x));
			return !existeRepetido;
		}
	}
}
