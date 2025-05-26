
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
public class FlightAssignmentPublishService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private FlightAssignmentRepository repository;


	@Override
	public void authorise() {
		// Must have request data and be a FlightCrewMember

		if (super.getRequest().getMethod().equals("GET"))
			super.state(false, "*", "flight-crew-member.flight-assignment.error");
		var principal = super.getRequest().getPrincipal();
		boolean hasId = super.getRequest().hasData("id", int.class);
		if (!hasId) {
			super.getResponse().setAuthorised(false);
			return;
		}
		int id = super.getRequest().getData("id", int.class);
		FlightAssignment assignment = this.repository.findById(id);
		if (assignment != null) {
			super.getResponse().setAuthorised(!assignment.getCurrentStatus().equals("CONFIRMED") && assignment.getFlightCrewMember().getId() == principal.getActiveRealm().getId());
			return;
		}
		super.getResponse().setAuthorised(hasId && principal.hasRealmOfType(FlightCrewMember.class));
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		FlightAssignment assignment = this.repository.findById(id);
		if (assignment != null)
			//this.updateToConfirmed(assignment);
			super.getBuffer().addData(assignment);
	}

	@Override
	public void bind(final FlightAssignment assignment) {
		super.bindObject(assignment, "remarks");
	}

	@Override
	public void validate(final FlightAssignment assignment) {
		if (super.getRequest().getCommand().equals("publish")) {
			FlightAssignment original = this.repository.findById(assignment.getId());

			boolean legUnModified = Objects.equals(super.getRequest().getData("leg", Leg.class), original.getLeg());
			boolean remarksUnModified = Objects.equals(assignment.getRemarks(), original.getRemarks());

			super.state(legUnModified, "leg", "flight-crew-member.flight-assignment.error-publishing");
			super.state(remarksUnModified, "remarks", "flight-crew-member.flight-assignment.error-publishing");
		}

	}

	@Override
	public void perform(final FlightAssignment assignment) {
		if (super.getRequest().getMethod().equals("GET"))
			return;
		this.updateToConfirmed(assignment);
		this.repository.save(assignment);
	}

	@Override
	public void onSuccess() {
		if ("POST".equalsIgnoreCase(super.getRequest().getMethod()))
			PrincipalHelper.handleUpdate();
	}

	// --- Helper ---
	private void updateToConfirmed(final FlightAssignment assignment) {
		assignment.setMomentOfLastUpdate(MomentHelper.getCurrentMoment());
		assignment.setCurrentStatus("CONFIRMED");
	}

	@Override
	public void unbind(final FlightAssignment assignment) {
		Dataset data = super.unbindObject(assignment, "momentOfLastUpdate", "currentStatus", "remarks");

		var allAssignments = this.findAssignmentsForLeg(assignment);

		FlightAssignment original = this.repository.findById(assignment.getId());
		data.put("leg", this.repository.findLegById(original.getLeg().getId()));

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
