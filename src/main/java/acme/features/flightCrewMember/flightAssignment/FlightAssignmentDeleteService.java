
package acme.features.flightCrewMember.flightAssignment;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.helpers.MomentHelper;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightAssignment.FlightAssignment;
import acme.entities.flightAssignment.FlightAssignmentDuty;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiService
public class FlightAssignmentDeleteService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private FlightAssignmentRepository repository;


	@Override
	public void authorise() {
		var request = super.getRequest();
		var principal = request.getPrincipal();

		System.out.println(super.getRequest().getUrl());
		System.out.println(super.getRequest());

		// Must have id and be a flight crew member
		if (!request.hasData("id", int.class) || !principal.hasRealmOfType(FlightCrewMember.class)) {
			super.getResponse().setAuthorised(false);
			return;
		}

		int id = request.getData("id", int.class);
		FlightAssignment assignment = this.repository.findById(id);

		// Must exist and not be confirmed
		if (assignment == null || "CONFIRMED".equals(assignment.getCurrentStatus())) {
			super.getResponse().setAuthorised(false);
			return;
		}

		int userId = principal.getActiveRealm().getId();
		int leadId = this.findLeadAttendantId(assignment);

		// Only the assigned member or lead attendant can delete
		if (assignment.getFlightCrewMember().getId() != userId && leadId != userId) {
			super.getResponse().setAuthorised(false);
			return;
		}

		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		FlightAssignment assignment = this.repository.findById(id);
		if (assignment != null) {
			this.markAsCancelled(assignment);
			super.getBuffer().addData(assignment);
		}
	}

	@Override
	public void bind(final FlightAssignment assignment) {
		// Nothing to bind beyond id
	}

	@Override
	public void validate(final FlightAssignment assignment) {
		// No additional validation
	}

	@Override
	public void perform(final FlightAssignment ignored) {
		int id = super.getRequest().getData("id", int.class);
		FlightAssignment assignment = this.repository.findById(id);
		if (assignment == null)
			return;

		this.markAsCancelled(assignment);
		this.repository.save(assignment);

		// If deleting lead attendant, cancel all assignments of the same leg
		if (FlightAssignmentDuty.LEAD_ATTENDANT.toString().equals(assignment.getDuty()))
			this.repository.findFlightAssignmentsOfLeg(assignment.getLeg().getId()).forEach(fa -> {
				this.markAsCancelled(fa);
				this.repository.save(fa);
			});
	}

	@Override
	public void onSuccess() {
		if ("POST".equalsIgnoreCase(super.getRequest().getMethod()))
			PrincipalHelper.handleUpdate();
	}

	// --- Helpers ---

	private void markAsCancelled(final FlightAssignment assignment) {
		assignment.setMomentOfLastUpdate(MomentHelper.getCurrentMoment());
		assignment.setCurrentStatus("CANCELLED");
	}

	private int findLeadAttendantId(final FlightAssignment assignment) {
		return this.repository.findFlightAssignmentsOfLeg(assignment.getLeg().getId()).stream().filter(fa -> "LEAD ATTENDANT".equals(fa.getDuty())).filter(fa -> !"CANCELLED".equals(fa.getCurrentStatus())).findFirst()
			.map(fa -> fa.getFlightCrewMember().getId()).orElse(-1);
	}

}
