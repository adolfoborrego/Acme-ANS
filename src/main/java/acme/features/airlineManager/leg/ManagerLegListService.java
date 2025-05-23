
package acme.features.airlineManager.leg;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flight.Flight;
import acme.entities.leg.Leg;
import acme.realms.airlineManager.AirlineManager;

@GuiService
public class ManagerLegListService extends AbstractGuiService<AirlineManager, Leg> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ManagerLegRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		int flightId = super.getRequest().getData("id", int.class);
		Flight flight = this.repository.findFlightById(flightId);

		int userAccountId;
		int managerId;

		userAccountId = super.getRequest().getPrincipal().getAccountId();
		managerId = this.repository.findManagerByUsserAccountId(userAccountId);

		boolean authorised = flight != null && super.getRequest().getPrincipal().hasRealm(flight.getAirlineManager()) && flight.getAirlineManager().getId() == managerId;

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		int flightId = super.getRequest().getData("id", int.class);
		Collection<Leg> legs = this.repository.findLegsByFlightIdOrderedByMoment(flightId);
		super.getBuffer().addData(legs);
	}

	@Override
	public void unbind(final Leg leg) {
		assert leg != null;

		Dataset dataset = super.unbindObject(leg, "scheduledDeparture", "scheduledArrival", "status");

		int flightId = super.getRequest().getData("id", int.class);
		Flight flight = this.repository.findFlightById(flightId);

		dataset.put("flightId", flightId);

		dataset.put("arrivalAirport", leg.getArrivalAirport().getName());
		dataset.put("departureAirport", leg.getDepartureAirport().getName());
		dataset.put("duration", leg.getDuration());
		dataset.put("flightNumber", leg.getFlightNumber());
		boolean showCreate = super.getRequest().getPrincipal().hasRealm(flight.getAirlineManager()) && !flight.getPublished();

		super.getResponse().addGlobal("showCreate", showCreate);

		super.getResponse().addGlobal("id", flightId);
		super.getResponse().addData(dataset);
	}

}
