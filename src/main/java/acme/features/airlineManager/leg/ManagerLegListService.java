
package acme.features.airlineManager.leg;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
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
		boolean status;

		status = super.getRequest().getPrincipal().hasRealmOfType(AirlineManager.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int flightId = super.getRequest().getData("flightId", int.class);
		Collection<Leg> legs = this.repository.findLegsByFlightIdOrderedByMoment(flightId);
		super.getBuffer().addData(legs);
	}

	@Override
	public void unbind(final Leg leg) {
		assert leg != null;

		Dataset dataset = super.unbindObject(leg, "scheduledDeparture", "scheduledArrival", "status");

		int flightId = super.getRequest().getData("flightId", int.class);
		dataset.put("flightId", flightId);

		dataset.put("arrivalAirport", leg.getArrivalAirport().getName());
		dataset.put("departureAirport", leg.getDepartureAirport().getName());
		dataset.put("duration", leg.getDuration());
		dataset.put("flightNumber", leg.getFlightNumber());

		boolean showCreate = super.getRequest().getPrincipal().hasRealm(leg.getFlight().getAirlineManager());
		super.getResponse().addGlobal("showCreate", showCreate);
		super.getResponse().addGlobal("flightId", flightId);
		super.getResponse().addData(dataset);
	}

}
