
package acme.features.flightCrewMember.flightAssignment;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightAssignment.FlightAssignment;
import acme.entities.leg.Leg;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiService
public class FlightAssignmentShowService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private FlightAssignmentRepository repository;


	@Override
	public void authorise() {
		var principal = super.getRequest().getPrincipal();
		if (!principal.hasRealmOfType(FlightCrewMember.class)) {
			super.getResponse().setAuthorised(false);
			return;
		}

		int assignmentId = super.getRequest().getData("id", int.class);
		FlightAssignment assignment = this.repository.findById(assignmentId);
		if (assignment == null || "CANCELLED".equals(assignment.getCurrentStatus())) {
			super.getResponse().setAuthorised(false);
			return;
		}

		int memberId = principal.getActiveRealm().getId();
		int legId = assignment.getLeg().getId();
		boolean belongs = this.repository.findFlightAssignmentsOfLeg(legId).stream().anyMatch(fa -> fa.getFlightCrewMember().getId() == memberId);

		super.getResponse().setAuthorised(belongs);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		FlightAssignment assignment = this.repository.findById(id);
		super.getBuffer().addData(assignment);
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
		Collection<Leg> legs = this.repository.findAllFutureUnAssignedOrAllCancelledLegs(MomentHelper.getCurrentMoment());
		legs.add(assignment.getLeg());
		return SelectChoices.from(legs, "identificator", assignment.getLeg());
	}
}
