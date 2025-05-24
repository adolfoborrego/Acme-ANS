
package acme.features.flightCrewMember.flightAssignment;

import java.util.Collection;
import java.util.Date;

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

	@Autowired
	private FlightAssignmentRepository repository;


	@Override
	public void authorise() {

		var principal = super.getRequest().getPrincipal();
		if (!principal.hasRealmOfType(FlightCrewMember.class)) {
			super.getResponse().setAuthorised(false);
			return;
		}

		if (super.getRequest().hasData("leg", int.class)) {
			int legId = super.getRequest().getData("leg", int.class);
			if (legId != 0 && !this.isLegSelectable(legId)) {
				super.getResponse().setAuthorised(false);
				return;
			}
		}

		if (super.getRequest().hasData("id", int.class) && super.getRequest().getData("id", int.class) != 0) {
			super.getResponse().setAuthorised(false);
			return;
		}

		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		var assignment = new FlightAssignment();
		this.initDefaults(assignment);
		super.getBuffer().addData(assignment);
	}

	@Override
	public void bind(final FlightAssignment assignment) {
		super.bindObject(assignment, "leg", "remarks");
	}

	@Override
	public void validate(final FlightAssignment assignment) {
		// No additional validation
	}

	@Override
	public void perform(final FlightAssignment assignment) {
		this.initDefaults(assignment);
		int legId = super.getRequest().getData("leg", int.class);
		assignment.setLeg(this.repository.findLegById(legId));
		this.repository.save(assignment);
	}

	@Override
	public void unbind(final FlightAssignment assignment) {
		Dataset data = super.unbindObject(assignment, "duty", "leg", "remarks");
		data.put("duty", this.getLeadChoice());
		data.put("possibleLegs", this.getPossibleLegChoices(assignment));
		data.put("statuses", this.getStatusChoices(assignment));
		super.getResponse().addData(data);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equalsIgnoreCase("POST"))
			PrincipalHelper.handleUpdate();
	}

	// --- Helpers ---

	private void initDefaults(final FlightAssignment assignment) {
		assignment.setMomentOfLastUpdate(MomentHelper.getCurrentMoment());
		assignment.setCurrentStatus(FlightAssignmentStatus.PENDING.toString());
		assignment.setDuty(FlightAssignmentDuty.LEAD_ATTENDANT.toString());
		int memberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		assignment.setFlightCrewMember(this.repository.findFlightCrewMemberById(memberId));
	}

	private boolean isLegSelectable(final int legId) {
		var leg = this.repository.findLegById(legId);
		var temp = new FlightAssignment();
		this.initDefaults(temp);
		temp.setLeg(this.repository.findLegById(legId));
		return leg != null && this.getPossibleLegs(temp).contains(leg);
	}

	private Collection<Leg> getPossibleLegs(final FlightAssignment assignment) {
		int memberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		FlightCrewMember member = this.repository.findFlightCrewMemberById(memberId);
		Date now = MomentHelper.getCurrentMoment();
		return this.repository.findAllFutureUnAssignedOrAllCancelledLegs(now).stream().filter(leg -> !this.hasOverlap(leg, memberId) && leg.getAircraft().getAirline().equals(member.getAirline())).toList();
	}

	private boolean hasOverlap(final Leg leg, final int memberId) {
		Date now = MomentHelper.getCurrentMoment();
		return this.repository.findAllMyPlannedFlightAssignments(memberId, now).stream().map(FlightAssignment::getLeg)
			.anyMatch(myLeg -> leg.getScheduledDeparture().before(myLeg.getScheduledArrival()) && leg.getScheduledArrival().after(myLeg.getScheduledDeparture()));
	}

	private SelectChoices getLeadChoice() {
		return SelectChoices.from(FlightAssignmentDuty.class, FlightAssignmentDuty.LEAD_ATTENDANT);
	}

	private SelectChoices getPossibleLegChoices(final FlightAssignment assignment) {
		return SelectChoices.from(this.getPossibleLegs(assignment), "identificator", assignment.getLeg());
	}

	private SelectChoices getStatusChoices(final FlightAssignment assignment) {
		var status = FlightAssignmentStatus.valueOf(assignment.getCurrentStatus());
		return SelectChoices.from(FlightAssignmentStatus.class, status);
	}
}
