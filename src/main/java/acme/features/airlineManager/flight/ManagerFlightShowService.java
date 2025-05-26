
package acme.features.airlineManager.flight;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flight.Flight;
import acme.entities.leg.Leg;
import acme.realms.airlineManager.AirlineManager;

@GuiService
public class ManagerFlightShowService extends AbstractGuiService<AirlineManager, Flight> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ManagerFlightRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int flightId;
		Flight flight;
		int userAccountId;
		int managerId;

		flightId = super.getRequest().getData("id", int.class);

		flight = this.repository.findFlightById(flightId);

		userAccountId = super.getRequest().getPrincipal().getAccountId();
		managerId = this.repository.findManagerByUsserAccountId(userAccountId);

		status = false;
		if (flight != null)
			status = flight.getAirlineManager().getId() == managerId;
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Flight flight;
		int id;

		id = super.getRequest().getData("id", int.class);
		flight = this.repository.findFlightById(id);

		super.getBuffer().addData(flight);
	}

	@Override
	public void unbind(final Flight flight) {
		assert flight != null;

		Dataset dataset = super.unbindObject(flight, "tag", "indicator", "cost", "description", "published");
		dataset.put("sheduledDeparture", flight.getSheduledDeparture());
		dataset.put("sheduledArrival", flight.getSheduledArrival());
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
