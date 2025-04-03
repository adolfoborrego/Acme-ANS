
package acme.features.administrator.airline;

import java.util.List;

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
public class AdministratorAirlineCreateService extends AbstractGuiService<Administrator, Airline> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorAirlineRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;

		status = super.getRequest().getPrincipal().hasRealmOfType(Administrator.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Airline airline = new Airline();

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
		boolean confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "administrator.airline.form.error.confirmation");

		boolean noRepetidoIataCode = this.noRepetidoIataCode(airline);
		super.state(noRepetidoIataCode, "iataCode", "administrator.airline.repeated-iataCode");
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

	private boolean noRepetidoIataCode(final Airline airline) {
		List<String> iataCodes = this.repository.findAllAirlines().stream().map(Airline::getIataCode).toList();
		boolean existeRepetido = iataCodes.stream().anyMatch(x -> airline.getIataCode().equals(x));
		return !existeRepetido;

	}

}
