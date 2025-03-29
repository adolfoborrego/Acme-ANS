
package acme.features.airlineManager.leg;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
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

		boolean authorised = leg != null && super.getRequest().getPrincipal().hasRealm(leg.getFlight().getAirlineManager()) && !leg.getPublished(); // Solo si no está publicada

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

		boolean diferentesAeropuertos = !leg.getDepartureAirport().equals(leg.getArrivalAirport());
		super.state(diferentesAeropuertos, "arrivalAirport", "manager.leg.error.same-airports");

		boolean llegadaDespuesSalida = leg.getScheduledArrival().after(leg.getScheduledDeparture());
		super.state(llegadaDespuesSalida, "scheduledArrival", "manager.leg.error.arrival-before-departure");

		List<Leg> existingLegs = this.repository.findLegsByFlightId(leg.getFlight().getId());
		boolean noSolapamiento = existingLegs.stream()
			.allMatch(existingLeg -> existingLeg.getId() == leg.getId() || leg.getScheduledArrival().before(existingLeg.getScheduledDeparture()) || leg.getScheduledDeparture().after(existingLeg.getScheduledArrival()));
		super.state(noSolapamiento, "scheduledDeparture", "manager.leg.error.overlapping-legs");
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
		SelectChoices aircrafts = SelectChoices.from(this.repository.findAllAircraft(), "registrationNumber", leg.getAircraft());

		choices = SelectChoices.from(LegStatus.class, leg.getStatus());
		Dataset dataset = super.unbindObject(leg, "scheduledDeparture", "scheduledArrival", "status", "departureAirport", "arrivalAirport", "aircraft", "published");
		dataset.put("id", leg.getId());
		dataset.put("statuses", choices);
		dataset.put("departureAirports", departureAirports);
		dataset.put("arrivalAirports", arrivalAirports);
		dataset.put("aircrafts", aircrafts);

		super.getResponse().addData(dataset);
	}
}
