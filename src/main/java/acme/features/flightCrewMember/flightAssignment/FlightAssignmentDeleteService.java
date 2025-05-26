
package acme.features.flightCrewMember.flightAssignment;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
public class FlightAssignmentDeleteService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private FlightAssignmentRepository repository;


	@Override
	public void authorise() {
		var request = super.getRequest();
		var principal = request.getPrincipal();

		if (super.getRequest().getMethod().equals("GET"))
			super.state(false, "*", "flight-crew-member.flight-assignment.error");
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
		if (assignment.getFlightCrewMember().getId() != userId) {
			super.getResponse().setAuthorised(false);
			return;
		}

		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		FlightAssignment assignment = this.repository.findById(id);
		if (assignment != null)
			//this.markAsCancelled(assignment);
			super.getBuffer().addData(assignment);
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

		if (!super.getRequest().getMethod().equals("GET")) {

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

	@Override
	public void unbind(final FlightAssignment assignment) {
		Dataset data = super.unbindObject(assignment, "momentOfLastUpdate", "currentStatus", "leg", "remarks");

		var allAssignments = this.findAssignmentsForLeg(assignment);
		data.put("duty", assignment.getDuty().replace(" ", "_"));
		data.put("lead_attendant", this.findByDuty(allAssignments, "LEAD ATTENDANT"));
		data.put("isSupLA", this.isSupLA(assignment));
		data.put("Fid", this.findActiveAssignmentId(allAssignments));
		data.put("statuses", assignment.getCurrentStatus());
		data.put("possibleLegs", this.buildLegChoices(assignment));
		data.put("isLegChangeable", allAssignments.stream().filter(x -> !"CANCELLED".equals(x.getCurrentStatus())).collect(Collectors.toList()).size() == 1);

		super.getResponse().addData(data);
	}

	/**
	 * Comprueba si el usuario actual tiene un assignment de Lead Attendant
	 * para este leg **y** está CONFIRMED.
	 */
	private boolean isSupLA(final FlightAssignment assignment) {
		// Obtiene el FID (la propia asignación del usuario)
		int fid = this.findActiveAssignmentId(this.findAssignmentsForLeg(assignment));
		if (fid == -1)
			return false;
		FlightAssignment myAssign = this.repository.findById(fid);
		return "LEAD ATTENDANT".equals(myAssign.getDuty()) && "CONFIRMED".equals(myAssign.getCurrentStatus());
	}

	private Collection<FlightAssignment> findAssignmentsForLeg(final FlightAssignment assignment) {
		int legId = assignment.getLeg().getId();
		return this.repository.findFlightAssignmentsOfLeg(legId);
	}

	private FlightAssignment findByDuty(final Collection<FlightAssignment> assignments, final String duty) {
		return assignments.stream().filter(a -> duty.equals(a.getDuty())).findFirst().orElse(null);
	}

	private int findActiveAssignmentId(final Collection<FlightAssignment> assignments) {
		Integer supId = super.getRequest().getPrincipal().getActiveRealm().getId();
		return assignments.stream().filter(a -> a.getFlightCrewMember().getId() == supId).filter(a -> !"CANCELLED".equals(a.getCurrentStatus())).findFirst().map(FlightAssignment::getId).orElse(-1);
	}

	private SelectChoices buildLegChoices(final FlightAssignment assignment) {
		List<Leg> legs = this.getPossibleLegs(assignment);
		legs.add(assignment.getLeg());
		return SelectChoices.from(legs, "identificator", assignment.getLeg());
	}

	private List<Leg> getPossibleLegs(final FlightAssignment assignment) {
		int memberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		FlightCrewMember member = this.repository.findFlightCrewMemberById(memberId);
		Date now = MomentHelper.getCurrentMoment();
		return this.repository.findAllFutureUnAssignedOrAllCancelledLegs(now).stream().filter(leg -> !this.hasOverlap(leg, memberId) && leg.getAircraft().getAirline().equals(member.getAirline())).collect(Collectors.toList());
	}
	private boolean hasOverlap(final Leg leg, final int memberId) {
		Date now = MomentHelper.getCurrentMoment();
		return this.repository.findAllMyPlannedFlightAssignments(memberId, now).stream().map(FlightAssignment::getLeg)
			.anyMatch(myLeg -> leg.getScheduledDeparture().before(myLeg.getScheduledArrival()) && leg.getScheduledArrival().after(myLeg.getScheduledDeparture()));
	}

}
