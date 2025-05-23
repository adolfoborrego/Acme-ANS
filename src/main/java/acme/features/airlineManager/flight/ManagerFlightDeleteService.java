
package acme.features.airlineManager.flight;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flight.Flight;
import acme.realms.airlineManager.AirlineManager;

@GuiService
public class ManagerFlightDeleteService extends AbstractGuiService<AirlineManager, Flight> {

	@Autowired
	protected ManagerFlightRepository repository;


	@Override
	public void authorise() {
		int id = super.getRequest().getData("id", int.class);
		Flight flight = this.repository.findFlightById(id);

		int userAccountId;
		int managerId;

		userAccountId = super.getRequest().getPrincipal().getAccountId();
		managerId = this.repository.findManagerByUsserAccountId(userAccountId);

		boolean authorised = flight != null && super.getRequest().getPrincipal().hasRealm(flight.getAirlineManager()) && !flight.getPublished() && flight.getAirlineManager().getId() == managerId;

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		Flight flight = this.repository.findFlightById(id);
		super.getBuffer().addData(flight);
	}

	@Override
	public void bind(final Flight flight) {
		// No se necesita
	}

	@Override
	public void validate(final Flight flight) {
		// No se necesita
	}

	@Override
	public void perform(final Flight flight) {
		this.repository.delete(flight);
	}

	@Override
	public void unbind(final Flight flight) {
		Dataset dataset = super.unbindObject(flight, "tag", "indicator", "cost", "description");

		// Para mostrar atributos derivados si quieres
		dataset.put("scheduledDeparture", flight.getSheduledDeparture());
		dataset.put("scheduledArrival", flight.getSheduledArrival());
		dataset.put("departureCity", flight.getDepartureCity());
		dataset.put("arrivalCity", flight.getArrivalCity());
		dataset.put("numberOfLayovers", flight.getNumberOfLayovers());

		super.getResponse().addData(dataset);
	}
}
