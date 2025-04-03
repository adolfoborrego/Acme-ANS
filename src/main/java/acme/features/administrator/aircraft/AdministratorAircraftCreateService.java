
package acme.features.administrator.aircraft;

import java.util.List;

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
public class AdministratorAircraftCreateService extends AbstractGuiService<Administrator, Aircraft> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorAircraftRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;

		status = super.getRequest().getPrincipal().hasRealmOfType(Administrator.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Aircraft aircraft = new Aircraft();

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
		boolean confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "administrator.aircraft.form.error.confirmation");

		boolean noRepetidoRegistrationNumber = this.noRepetidoRegistrationNumber(aircraft);
		super.state(noRepetidoRegistrationNumber, "registrationNumber", "administrator.aircraft.repeated-registrationNumber");
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

	private boolean noRepetidoRegistrationNumber(final Aircraft aircraft) {
		List<String> registrationNumbers = this.repository.findAllAircrafts().stream().map(Aircraft::getRegistrationNumber).toList();
		boolean existeRepetido = registrationNumbers.stream().anyMatch(x -> aircraft.getRegistrationNumber().equals(x));
		return !existeRepetido;

	}
}
