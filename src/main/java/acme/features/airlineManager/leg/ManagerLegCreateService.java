
package acme.features.airlineManager.leg;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.aircraft.Aircraft;
import acme.entities.aircraft.AircraftStatus;
import acme.entities.flight.Flight;
import acme.entities.leg.Leg;
import acme.entities.leg.LegStatus;
import acme.realms.airlineManager.AirlineManager;

@GuiService
public class ManagerLegCreateService extends AbstractGuiService<AirlineManager, Leg> {

	@Autowired
	protected ManagerLegRepository repository;


	@Override
	public void authorise() {
		int flightId = super.getRequest().getData("id", int.class);
		Flight flight = this.repository.findFlightById(flightId);
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

		boolean authorised = flight != null && super.getRequest().getPrincipal().hasRealm(flight.getAirlineManager()) && !flight.getPublished() && flight.getAirlineManager().getId() == managerId && aircraftActive;

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		int flightId = super.getRequest().getData("id", int.class);
		Flight flight = this.repository.findFlightById(flightId);

		Leg leg = new Leg();
		leg.setFlight(flight);
		leg.setPublished(false);

		super.getBuffer().addData(leg);
	}

	@Override
	public void validate(final Leg leg) {
		assert leg != null;

		// Validar que los aeropuertos sean diferentes
		if (leg.getDepartureAirport() != null && leg.getArrivalAirport() != null) {
			boolean diferentesAeropuertos = !leg.getDepartureAirport().equals(leg.getArrivalAirport());
			super.state(diferentesAeropuertos, "arrivalAirport", "manager.leg.error.same-airports");
		}

		// Validar que la llegada sea después de la salida
		if (leg.getScheduledArrival() != null && leg.getScheduledDeparture() != null) {
			boolean llegadaDespuesSalida = leg.getScheduledArrival().after(leg.getScheduledDeparture());
			super.state(llegadaDespuesSalida, "scheduledArrival", "manager.leg.error.arrival-before-departure");
		}

		// Validar que no haya solapamientos solo si se tienen ambas fechas
		if (leg.getScheduledArrival() != null && leg.getScheduledDeparture() != null && leg.getFlight() != null) {
			List<Leg> existingLegs = this.repository.findLegsByFlightId(leg.getFlight().getId());
			boolean noSolapamiento = existingLegs.stream()
				.allMatch(existingLeg -> existingLeg.getId() == leg.getId() || leg.getScheduledArrival().before(existingLeg.getScheduledDeparture()) || leg.getScheduledDeparture().after(existingLeg.getScheduledArrival()));
			super.state(noSolapamiento, "scheduledDeparture", "manager.leg.error.overlapping-legs");
		}
	}

	@Override
	public void bind(final Leg leg) {
		assert leg != null;
		super.bindObject(leg, "scheduledDeparture", "scheduledArrival", "status", "departureAirport", "arrivalAirport", "aircraft");
	}

	@Override
	public void perform(final Leg leg) {
		this.repository.save(leg);
	}

	@Override
	public void unbind(final Leg leg) {
		assert leg != null;

		SelectChoices statuses = SelectChoices.from(LegStatus.class, leg.getStatus());
		SelectChoices departureAirports = SelectChoices.from(this.repository.findAllAirports(), "name", leg.getDepartureAirport());
		SelectChoices arrivalAirports = SelectChoices.from(this.repository.findAllAirports(), "name", leg.getArrivalAirport());
		SelectChoices aircrafts = SelectChoices.from(this.repository.findAllAircraft().stream().filter(x -> x.getStatus().equals(AircraftStatus.ACTIVE)).toList(), "registrationNumber", leg.getAircraft());

		Dataset dataset = super.unbindObject(leg, "scheduledDeparture", "scheduledArrival", "status", "departureAirport", "arrivalAirport", "aircraft", "published");
		dataset.put("id", leg.getFlight().getId());
		dataset.put("statuses", statuses);
		dataset.put("departureAirports", departureAirports);
		dataset.put("arrivalAirports", arrivalAirports);
		dataset.put("aircrafts", aircrafts);

		dataset.put("duration", leg.getDuration());
		dataset.put("flightNumber", leg.getFlightNumber());

		super.getResponse().addData(dataset);
	}

}
