
package acme.features.flightCrewMember.flightAssignment;

import java.util.ArrayList;
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
import acme.entities.leg.Leg;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiService
public class FlightAssignmentAddService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

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
		int flightAssignmentId = super.getRequest().getData("Fid", int.class);
		FlightAssignment f = this.repository.findById(flightAssignmentId);
		super.getBuffer().addData(f);
	}

	@Override
	public void bind(final FlightAssignment object) {
		assert object != null;
		super.bindObject(object, "duty");
	}

	@Override
	public void validate(final FlightAssignment object) {
		assert object != null;
		// Puedes dejarlo vacío por ahora
	}

	@Override
	public void perform(FlightAssignment object) {
		assert object != null;

		object = new FlightAssignment();
		object.setMomentOfLastUpdate(MomentHelper.getCurrentMoment());
		object.setCurrentStatus("PENDING");
		Integer memberId = super.getRequest().getData("memberId", int.class);
		FlightCrewMember flightCrewMember = this.repository.findFlightCrewMemberById(memberId);
		object.setFlightCrewMember(flightCrewMember);

		String duty = super.getRequest().getData("duty", String.class);
		object.setDuty(duty);

		Integer legId = super.getRequest().getData("leg", int.class);

		Leg leg = this.repository.findLegById(legId);
		object.setLeg(leg);

		this.repository.save(object);
	}

	@Override
	public void unbind(FlightAssignment flightAssignment) {
		int flightAssignmentId = super.getRequest().getData("Fid", int.class);
		flightAssignment = this.repository.findById(flightAssignmentId);
		Dataset dataset = super.unbindObject(flightAssignment, "leg");

		dataset.put("possibleLegs", this.getLegChoice(flightAssignment));
		dataset.put("readonly", this.isReadOnly(flightAssignment));
		dataset.put("remarks", "");
		dataset.put("duty", this.getStatusesSelectChoices(flightAssignment));
		dataset.put("possibleCrews", this.getPossibleCrewsSelectChoices(flightAssignment));
		super.getResponse().addData(dataset);
	}

	private Object getLegChoice(final FlightAssignment f) {
		// TODO Auto-generated method stubç
		Collection<Leg> l = new ArrayList<>();
		l.add(f.getLeg());
		return SelectChoices.from(l, "id", f.getLeg());
	}

	private boolean isReadOnly(final FlightAssignment flightAssignment) {
		Integer id = super.getRequest().getPrincipal().getActiveRealm().getId();
		flightAssignment.getFlightCrewMember().getId();
		return flightAssignment.getFlightCrewMember().getId() != id;
	}

	private Object getPossibleCrewsSelectChoices(final FlightAssignment flightAssignment) {
		Collection<FlightCrewMember> possibleCrews = this.repository.findCrewsMembersCandidateForLeg();
		return SelectChoices.from(possibleCrews, "id", null);
	}

	private SelectChoices getStatusesSelectChoices(final FlightAssignment flightAssignment) {
		return SelectChoices.from(FlightAssignmentDuty.class, null);
	}
	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equalsIgnoreCase("POST"))
			PrincipalHelper.handleUpdate();
	}
}
