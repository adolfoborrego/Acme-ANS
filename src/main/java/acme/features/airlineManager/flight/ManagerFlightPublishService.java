
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
public class ManagerFlightPublishService extends AbstractGuiService<AirlineManager, Flight> {

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

		boolean authorised = false;
		if (flight != null)
			authorised = !flight.getPublished() && super.getRequest().getPrincipal().hasRealm(flight.getAirlineManager()) && flight.getAirlineManager().getId() == managerId;

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
		// No binding necesario para publicación
	}

	@Override
	public void validate(final Flight flight) {
		int flightId = flight.getId();
		Collection<Leg> legs = this.repository.findLegsByFlightId(flightId);

		boolean hasLegs = !legs.isEmpty();
		boolean allPublished = legs.stream().allMatch(Leg::getPublished);

		super.state(hasLegs, "published", "manager.flight.publish.error.no-legs");
		super.state(allPublished, "published", "manager.flight.publish.error.unpublished-legs");
	}

	@Override
	public void perform(final Flight flight) {
		flight.setPublished(true);
		this.repository.save(flight);
	}

	@Override
	public void unbind(final Flight flight) {
		Dataset dataset = super.unbindObject(flight, "tag", "cost", "description", "indicator", "published");
		super.getResponse().addData(dataset);
	}
}
