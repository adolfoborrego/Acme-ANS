
package acme.features.airlineManager.leg;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircraft.AircraftStatus;
import acme.entities.leg.Leg;
import acme.entities.leg.LegStatus;
import acme.realms.airlineManager.AirlineManager;

@GuiService
public class ManagerLegDeleteService extends AbstractGuiService<AirlineManager, Leg> {

	@Autowired
	protected ManagerLegRepository repository;


	@Override
	public void authorise() {
		int id = super.getRequest().getData("id", int.class);
		Leg leg = this.repository.findLegById(id);

		int userAccountId;
		int managerId;

		userAccountId = super.getRequest().getPrincipal().getAccountId();
		managerId = this.repository.findManagerByUsserAccountId(userAccountId);

		boolean authorised = false;
		if (leg != null)
			authorised = super.getRequest().getPrincipal().hasRealm(leg.getFlight().getAirlineManager()) && !leg.getPublished() && leg.getFlight().getAirlineManager().getId() == managerId;
		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void bind(final Leg leg) {
		// Nada que bindear
	}

	@Override
	public void validate(final Leg leg) {
		// Nada que validar
	}

	@Override
	public void unbind(final Leg leg) {
		assert leg != null;

		SelectChoices statuses = SelectChoices.from(LegStatus.class, leg.getStatus());
		SelectChoices departureAirports = SelectChoices.from(this.repository.findAllAirports(), "name", leg.getDepartureAirport());
		SelectChoices arrivalAirports = SelectChoices.from(this.repository.findAllAirports(), "name", leg.getArrivalAirport());
		SelectChoices aircrafts = SelectChoices.from(this.repository.findAllAircraft().stream().filter(x -> x.getStatus().equals(AircraftStatus.ACTIVE)).toList(), "registrationNumber", leg.getAircraft());

		Dataset dataset = super.unbindObject(leg, "scheduledDeparture", "scheduledArrival", "status", "departureAirport", "arrivalAirport", "aircraft", "published");
		dataset.put("flightId", leg.getFlight().getId());
		dataset.put("statuses", statuses);
		dataset.put("departureAirports", departureAirports);
		dataset.put("arrivalAirports", arrivalAirports);
		dataset.put("aircrafts", aircrafts);

		dataset.put("duration", leg.getDuration());
		dataset.put("flightNumber", leg.getFlightNumber());

		super.getResponse().addData(dataset);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		Leg leg = this.repository.findLegById(id);
		super.getBuffer().addData(leg);
	}

	@Override
	public void perform(final Leg leg) {
		this.repository.delete(leg);
	}
}
