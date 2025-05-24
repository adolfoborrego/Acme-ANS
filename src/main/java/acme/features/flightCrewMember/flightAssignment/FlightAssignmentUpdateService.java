
package acme.features.flightCrewMember.flightAssignment;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightAssignment.FlightAssignment;
import acme.entities.leg.Leg;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiService
public class FlightAssignmentUpdateService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private FlightAssignmentRepository	repository;

	private Boolean						ok;


	@Override
	public void authorise() {
		var request = super.getRequest();
		var principal = request.getPrincipal();

		boolean validId = request.hasData("id", int.class);
		if (super.getRequest().getMethod().equals("GET"))
			super.state(false, "*", "flight-crew-member.flight-assignment.error");

		if (!validId || !principal.hasRealmOfType(FlightCrewMember.class)) {
			super.getResponse().setAuthorised(false);
			return;
		}

		int id = request.getData("id", int.class);
		FlightAssignment assignment = this.repository.findById(id);
		if (assignment == null || !"PENDING".equals(assignment.getCurrentStatus())) {
			super.getResponse().setAuthorised(false);
			return;
		}

		// Ensure the current user owns this assignment
		int userId = principal.getActiveRealm().getId();
		if (assignment.getFlightCrewMember().getId() != userId) {
			super.getResponse().setAuthorised(false);
			return;
		}

		if (request.hasData("leg", int.class)) {
			int legId = request.getData("leg", int.class);
			boolean allowed = legId == assignment.getLeg().getId() || this.getPossibleLegs(assignment).stream().anyMatch(l -> l.getId() == legId);
			if (legId != 0 && !allowed) {
				super.getResponse().setAuthorised(false);
				return;
			}
		}

		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		FlightAssignment assignment = this.repository.findById(id);
		this.resetToPending(assignment);
		super.getBuffer().addData(assignment);
	}

	@Override
	public void bind(final FlightAssignment assignment) {
		super.bindObject(assignment, "remarks");
	}

	@Override
	public void validate(final FlightAssignment assignment) {
		// No extra validation
		if (super.getRequest().getCommand().equals("update")) {
			FlightAssignment original = this.repository.findById(assignment.getId());

			Leg newLeg = super.getRequest().getData("leg", Leg.class);

			if (newLeg != null) {
				boolean isModified = !Objects.equals(newLeg, original.getLeg()) || !Objects.equals(assignment.getRemarks(), original.getRemarks());
				super.state(isModified, "*", "flight-crew-member.flight-assignment.error");
				this.ok = isModified;

			} else {
				boolean isModified = !Objects.equals(assignment.getRemarks(), original.getRemarks());
				super.state(false, "leg", "flight-crew-member.flight-assignment.errorUpdatingLeg");
				super.state(isModified, "*", "flight-crew-member.flight-assignment.error");
				this.ok = isModified;

			}

		}
	}

	@Override
	public void unbind(final FlightAssignment assignment) {
		Dataset data = super.unbindObject(assignment, "momentOfLastUpdate", "currentStatus", "remarks");

		FlightAssignment original = this.repository.findById(assignment.getId());
		data.put("leg", this.repository.findLegById(original.getLeg().getId()));

		Collection<FlightAssignment> assignments = this.getAssignmentsForLeg(assignment);
		data.put("duty", assignment.getDuty().replace(" ", "_"));
		data.put("lead_attendant", this.findByDuty(assignments, "LEAD ATTENDANT"));
		data.put("isSupLA", this.isSupLA(assignment));
		data.put("Fid", this.findActiveAssignmentId(assignments));
		data.put("statuses", assignment.getCurrentStatus());
		data.put("possibleLegs", this.buildLegChoices(assignment));
		data.put("isLegChangeable", assignments.size() == 1);

		super.getResponse().addData(data);
	}

	@Override
	public void perform(final FlightAssignment ignored) {
		int id = super.getRequest().getData("id", int.class);
		FlightAssignment assignment = this.repository.findById(id);
		this.resetToPending(assignment);

		int legId = super.getRequest().getData("leg", int.class);
		assignment.setLeg(this.repository.findLegById(legId));

		String remarks = super.getRequest().getData("remarks", String.class);
		assignment.setRemarks(remarks);

		this.repository.save(assignment);
	}

	@Override
	public void onSuccess() {
		if ("POST".equalsIgnoreCase(super.getRequest().getMethod()))
			PrincipalHelper.handleUpdate();
	}

	@Override
	public void onFailure() {
		if (!super.getRequest().getMethod().equals("GET") && this.ok.equals(true))
			super.state(false, "*", "flight-crew-member.flight-assignment.error");
	}

	// --- Helpers ---

	private void resetToPending(final FlightAssignment assignment) {
		assignment.setMomentOfLastUpdate(MomentHelper.getCurrentMoment());
		assignment.setCurrentStatus("PENDING");
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

	private Collection<FlightAssignment> getAssignmentsForLeg(final FlightAssignment assignment) {
		int legId = assignment.getLeg().getId();
		return this.repository.findFlightAssignmentsOfLeg(legId);
	}

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
		return assignments.stream().filter(a -> duty.equals(a.getDuty().toString())).findFirst().orElse(null);
	}

	private int findActiveAssignmentId(final Collection<FlightAssignment> assignments) {
		Integer supId = super.getRequest().getPrincipal().getActiveRealm().getId();
		return assignments.stream().filter(a -> a.getFlightCrewMember().getId() == supId).filter(a -> !"CANCELLED".equals(a.getCurrentStatus())).findFirst().map(FlightAssignment::getId).orElse(-1);
	}

	private SelectChoices buildLegChoices(final FlightAssignment assignment) {
		List<Leg> legs = this.getPossibleLegs(assignment);
		legs.add(assignment.getLeg());
		if (!super.getRequest().getMethod().equals("GET"))
			return SelectChoices.from(legs, "identificator", super.getRequest().getData("leg", Leg.class));
		else
			return SelectChoices.from(legs, "identificator", assignment.getLeg());

	}
}
