
package acme.features.airlineManager.flight;

import java.util.Collection;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flight.Flight;
import acme.entities.leg.Leg;
import acme.realms.airlineManager.AirlineManager;

@GuiService
public class ManagerFlightUpdateService extends AbstractGuiService<AirlineManager, Flight> {

	@Autowired
	protected ManagerFlightRepository repository;


	@Override
	public void authorise() {
		int id = super.getRequest().getData("id", int.class);
		Flight flight = this.repository.findFlightById(id);

		boolean authorised = flight != null && super.getRequest().getPrincipal().hasRealm(flight.getAirlineManager()) && !flight.getPublished();

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
		assert flight != null;
		super.bindObject(flight, "tag", "indicator", "cost", "description");
	}

	@Override
	public void validate(final Flight flight) {
		assert flight != null;
		if (super.getRequest().getCommand().equalsIgnoreCase("update")) {
			Flight original = this.repository.findFlightById(flight.getId());

			boolean isModified = !Objects.equals(flight.getTag(), original.getTag()) || !Objects.equals(flight.getDescription(), original.getDescription()) || !Objects.equals(flight.getIndicator(), original.getIndicator())
				|| !Objects.equals(flight.getCost().getAmount(), original.getCost().getAmount()) || !Objects.equals(flight.getCost().getCurrency(), original.getCost().getCurrency());

			super.state(isModified, "*", "manager.flight.error.no-changes");
		}
	}

	@Override
	public void perform(final Flight flight) {
		assert flight != null;
		this.repository.save(flight);
	}

	@Override
	public void unbind(final Flight flight) {
		Dataset dataset = super.unbindObject(flight, "tag", "indicator", "cost", "description");

		// Para mostrar atributos derivados si quieres
		dataset.put("id", flight.getId());
		dataset.put("scheduledDeparture", flight.getSheduledDeparture());
		dataset.put("scheduledArrival", flight.getSheduledArrival());
		dataset.put("departureCity", flight.getDepartureCity());
		dataset.put("arrivalCity", flight.getArrivalCity());
		dataset.put("numberOfLayovers", flight.getNumberOfLayovers());

		Collection<Leg> legs = this.repository.findLegsByFlightId(flight.getId());
		boolean hasLegs = !legs.isEmpty();
		boolean allPublished = hasLegs && legs.stream().allMatch(Leg::getPublished);
		boolean canPublish = !flight.getPublished() && allPublished;

		dataset.put("canPublish", canPublish);

		super.getResponse().addData(dataset);
	}

}
