
package acme.features.airlineManager.leg;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircraft.Aircraft;
import acme.entities.aircraft.AircraftStatus;
import acme.entities.leg.Leg;
import acme.entities.leg.LegStatus;
import acme.realms.airlineManager.AirlineManager;

@GuiService
public class ManagerLegUpdateService extends AbstractGuiService<AirlineManager, Leg> {

	@Autowired
	protected ManagerLegRepository repository;


	@Override
	public void authorise() {
		int id = super.getRequest().getData("id", int.class);
		Leg leg = this.repository.findLegById(id);

		int aircraftId = 0;
		if (super.getRequest().hasData("aircraft"))
			aircraftId = super.getRequest().getData("aircraft", int.class);

		Aircraft aircraft = this.repository.findAircraftById(aircraftId);
		boolean aircraftActive = true;
		if (aircraft != null)
			aircraftActive = aircraft.getStatus().equals(AircraftStatus.ACTIVE);

		int userAccountId;
		int managerId;

		userAccountId = super.getRequest().getPrincipal().getAccountId();
		managerId = this.repository.findManagerByUsserAccountId(userAccountId);

		boolean authorised = leg != null && super.getRequest().getPrincipal().hasRealm(leg.getFlight().getAirlineManager()) && !leg.getPublished() && leg.getFlight().getAirlineManager().getId() == managerId && aircraftActive; // Solo si no está publicada

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		Leg leg = this.repository.findLegById(id);
		super.getBuffer().addData(leg);
	}

	@Override
	public void bind(final Leg leg) {
		super.bindObject(leg, "scheduledDeparture", "scheduledArrival", "status", "departureAirport", "arrivalAirport", "aircraft", "published");
	}

	@Override
	public void validate(final Leg leg) {
		assert leg != null;

		if (leg.getDepartureAirport() != null && leg.getArrivalAirport() != null) {
			boolean diferentesAeropuertos = !leg.getDepartureAirport().equals(leg.getArrivalAirport());
			super.state(diferentesAeropuertos, "arrivalAirport", "manager.leg.error.same-airports");
		}

		if (leg.getScheduledArrival() != null && leg.getScheduledDeparture() != null) {
			boolean llegadaDespuesSalida = leg.getScheduledArrival().after(leg.getScheduledDeparture());
			super.state(llegadaDespuesSalida, "scheduledArrival", "manager.leg.error.arrival-before-departure");
		}

		List<Leg> existingLegs = this.repository.findLegsByFlightId(leg.getFlight().getId());
		if (leg.getScheduledArrival() != null && leg.getScheduledDeparture() != null) {
			boolean noSolapamiento = existingLegs.stream()
				.allMatch(existingLeg -> existingLeg.getId() == leg.getId() || leg.getScheduledArrival().before(existingLeg.getScheduledDeparture()) || leg.getScheduledDeparture().after(existingLeg.getScheduledArrival()));
			super.state(noSolapamiento, "scheduledDeparture", "manager.leg.error.overlapping-legs");
		}

		// if (super.getRequest().getCommand().equals("update")) {
		//Leg original = this.repository.findLegById(leg.getId());

		//boolean isModified = !Objects.equals(leg.getScheduledDeparture(), original.getScheduledDeparture()) || !Objects.equals(leg.getScheduledArrival(), original.getScheduledArrival())
		//	|| !Objects.equals(leg.getDepartureAirport(), original.getDepartureAirport()) || !Objects.equals(leg.getArrivalAirport(), original.getArrivalAirport()) || !Objects.equals(leg.getStatus(), original.getStatus());

		//super.state(isModified, "*", "manager.leg.error.no-changes");
		//}
	}

	@Override
	public void perform(final Leg leg) {
		this.repository.save(leg);
	}

	@Override
	public void unbind(final Leg leg) {
		SelectChoices choices;

		SelectChoices departureAirports = SelectChoices.from(this.repository.findAllAirports(), "name", leg.getDepartureAirport());
		SelectChoices arrivalAirports = SelectChoices.from(this.repository.findAllAirports(), "name", leg.getArrivalAirport());
		SelectChoices aircrafts = SelectChoices.from(this.repository.findAllAircraft().stream().filter(x -> x.getStatus().equals(AircraftStatus.ACTIVE)).toList(), "registrationNumber", leg.getAircraft());

		choices = SelectChoices.from(LegStatus.class, leg.getStatus());
		Dataset dataset = super.unbindObject(leg, "scheduledDeparture", "scheduledArrival", "status", "departureAirport", "arrivalAirport", "aircraft", "published");
		dataset.put("id", leg.getId());
		dataset.put("statuses", choices);
		dataset.put("departureAirports", departureAirports);
		dataset.put("arrivalAirports", arrivalAirports);
		dataset.put("aircrafts", aircrafts);

		dataset.put("duration", leg.getDuration());
		dataset.put("flightNumber", leg.getFlightNumber());

		super.getResponse().addData(dataset);
	}
}
