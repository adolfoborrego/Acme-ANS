
package acme.features.flightCrewMember.flightAssignment;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightAssignment.FlightAssignment;
import acme.entities.flightAssignment.FlightAssignmentDuty;
import acme.entities.flightAssignment.FlightAssignmentStatus;
import acme.entities.leg.Leg;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiService
public class FlightAssignmentCreateService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private FlightAssignmentRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		FlightAssignment object = new FlightAssignment();
		object.setMomentOfLastUpdate(MomentHelper.getCurrentMoment());
		object.setCurrentStatus("PENDING");
		Integer memberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		FlightCrewMember flightCrewMember = this.repository.findFlightCrewMemberById(memberId);
		object.setFlightCrewMember(flightCrewMember);
		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final FlightAssignment object) {
		assert object != null;
		super.bindObject(object, "duty", "leg", "remarks");
	}

	@Override
	public void validate(final FlightAssignment object) {
		assert object != null;
		// Puedes dejarlo vacío por ahora
	}

	@Override
	public void perform(final FlightAssignment object) {
		assert object != null;

		object.setMomentOfLastUpdate(MomentHelper.getCurrentMoment());
		object.setCurrentStatus("PENDING");
		Integer memberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		FlightCrewMember flightCrewMember = this.repository.findFlightCrewMemberById(memberId);
		object.setFlightCrewMember(flightCrewMember);

		Integer legId = super.getRequest().getData("leg", int.class);

		Leg leg = this.repository.findLegById(legId);
		object.setLeg(leg);

		this.repository.save(object);
	}

	@Override
	public void unbind(final FlightAssignment flightAssignment) {

		Dataset dataset = super.unbindObject(flightAssignment, "duty", "leg", "remarks");

		dataset.put("readonly", this.isReadOnly(flightAssignment));
		dataset.put("duty", this.getLeadAttendantSelectChoice());
		dataset.put("possibleCrews", null);
		dataset.put("possibleLegs", this.getPossibleLegsSelectChoices(flightAssignment));
		dataset.put("statuses", this.getStatusesSelectChoices(flightAssignment));

		super.getResponse().addData(dataset);
	}

	private Object getLeadAttendantSelectChoice() {
		// TODO Auto-generated method stub
		return SelectChoices.from(FlightAssignmentDuty.class, FlightAssignmentDuty.LEAD_ATTENDANT);
	}

	private boolean isReadOnly(final FlightAssignment flightAssignment) {
		// Si  no es "LEAD ATTENDANT", se considera de solo lectura
		return false;
	}

	private Object getPossibleLegsSelectChoices(final FlightAssignment flightAssignment) {
		// Se utiliza ArgumentMatchers.anyCollection() como marcador; 
		// luego se añade el leg actual para que sea la única opción disponible.
		Collection<Leg> possibleLegs = this.repository.findAllFutureUnAssignLegs();
		return SelectChoices.from(possibleLegs, "id", flightAssignment.getLeg());
	}

	private SelectChoices getStatusesSelectChoices(final FlightAssignment flightAssignment) {
		FlightAssignmentStatus current = Enum.valueOf(FlightAssignmentStatus.class, flightAssignment.getCurrentStatus());
		return SelectChoices.from(FlightAssignmentStatus.class, current);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equalsIgnoreCase("POST"))
			PrincipalHelper.handleUpdate();
	}
}
