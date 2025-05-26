
package acme.features.flightCrewMember.flightAssignment;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightAssignment.FlightAssignment;
import acme.entities.flightAssignment.FlightAssignmentDuty2;
import acme.entities.leg.Leg;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiService
public class FlightAssignmentAddService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private FlightAssignmentRepository repository;


	@Override
	public void authorise() {

		var request = super.getRequest();
		var principal = request.getPrincipal();

		boolean authorised = principal.hasRealmOfType(FlightCrewMember.class) && request.hasData("Fid");

		if (authorised) {
			int fid = request.getData("Fid", int.class);
			FlightAssignment assignment = this.repository.findById(fid);
			int memberId = principal.getActiveRealm().getId();

			authorised = assignment != null && "CONFIRMED".equals(assignment.getCurrentStatus()) && "LEAD ATTENDANT".equals(assignment.getDuty()) && assignment.getFlightCrewMember().getId() == memberId;

			if (authorised && request.hasData("memberId", int.class)) {
				int selectedMemberId = request.getData("memberId", int.class);
				if (selectedMemberId != 0) {
					Integer myId = super.getRequest().getPrincipal().getActiveRealm().getId();
					FlightCrewMember member = this.repository.findFlightCrewMemberById(myId);
					Integer airlineId = member.getAirline().getId();
					Leg leg = assignment.getLeg();
					Collection<FlightCrewMember> crews = this.repository.findCrewsMembersCandidateForLegAvoidingOverlaps(airlineId, leg.getScheduledDeparture(), leg.getScheduledArrival());
					List<Integer> possibleCrews = crews.stream().map(FlightCrewMember::getId).toList();
					authorised = possibleCrews.contains(selectedMemberId);
					if (super.getRequest().hasData("duty", String.class))
						authorised = authorised && !super.getRequest().getData("duty", String.class).equals("LEAD ATTENDANT");
				}
			}
		}

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("Fid", int.class);
		FlightAssignment assignment = this.repository.findById(id);
		super.getBuffer().addData(assignment);
	}

	@Override
	public void bind(final FlightAssignment object) {
		super.bindObject(object, "duty");
	}

	@Override
	public void validate(final FlightAssignment object) {
		int memberId = super.getRequest().getData("memberId", int.class);

		if (memberId == 0) {
			super.state(false, "memberId", "flight-assignment.member.error.unselected");
			return;
		}

		int fId = super.getRequest().getData("id", int.class);
		FlightAssignment currentAssignment = this.repository.findById(fId);
		Collection<FlightAssignment> assignments = this.repository.findFlightAssignmentsOfLeg(currentAssignment.getLeg().getId());

		assignments = assignments.stream().filter(a -> !"CANCELLED".equals(a.getCurrentStatus())).toList();

		if ("PILOT".equals(object.getDuty()) && assignments.stream().anyMatch(a -> "PILOT".equals(a.getDuty()))) {
			super.state(false, "duty", "flight-assignment.member.error.pilot");
			return;
		}

		if ("COPILOT".equals(object.getDuty()) && assignments.stream().anyMatch(a -> "COPILOT".equals(a.getDuty()))) {
			super.state(false, "duty", "flight-assignment.member.error.copilot");
			return;
		}

		long cabinAttendants = assignments.stream().filter(a -> "CABIN ATTENDANT".equals(a.getDuty())).count();

		if (cabinAttendants >= 4 && "CABIN ATTENDANT".equals(object.getDuty()))
			super.state(false, "duty", "flight-assignment.member.error.cabinAttendant");
	}

	@Override
	public void perform(FlightAssignment object) {
		object = new FlightAssignment();

		object.setMomentOfLastUpdate(MomentHelper.getCurrentMoment());
		object.setCurrentStatus("PENDING");

		int memberId = super.getRequest().getData("memberId", int.class);
		FlightCrewMember member = this.repository.findFlightCrewMemberById(memberId);
		object.setFlightCrewMember(member);

		object.setDuty(super.getRequest().getData("duty", String.class));

		int legId = super.getRequest().getData("leg", int.class);
		Leg leg = this.repository.findLegById(legId);
		object.setLeg(leg);

		this.repository.save(object);
	}

	@Override
	public void unbind(FlightAssignment flightAssignment) {
		int id = super.getRequest().getData("Fid", int.class);
		flightAssignment = this.repository.findById(id);

		Dataset dataset = super.unbindObject(flightAssignment, "leg");
		dataset.put("possibleLegs", SelectChoices.from(List.of(flightAssignment.getLeg()), "identificator", flightAssignment.getLeg()));
		dataset.put("readonly", this.isReadOnly(flightAssignment));
		dataset.put("remarks", "");
		dataset.put("duty", this.getDutyChoices());
		dataset.put("possibleCrews", this.getCrewChoices());

		super.getResponse().addData(dataset);
	}

	private boolean isReadOnly(final FlightAssignment assignment) {
		int principalId = super.getRequest().getPrincipal().getActiveRealm().getId();
		return assignment.getFlightCrewMember().getId() != principalId;
	}

	private SelectChoices getCrewChoices() {
		Integer myId = super.getRequest().getPrincipal().getActiveRealm().getId();
		FlightCrewMember member = this.repository.findFlightCrewMemberById(myId);
		Integer airlineId = member.getAirline().getId();
		int fid = super.getRequest().getData("Fid", int.class);
		FlightAssignment assignment = this.repository.findById(fid);
		Leg leg = assignment.getLeg();
		Collection<FlightCrewMember> crews = this.repository.findCrewsMembersCandidateForLegAvoidingOverlaps(airlineId, leg.getScheduledDeparture(), leg.getScheduledArrival());
		return SelectChoices.from(crews, "identificator", null);
	}

	private SelectChoices getDutyChoices() {
		return SelectChoices.from(FlightAssignmentDuty2.class, null);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equalsIgnoreCase("POST"))
			PrincipalHelper.handleUpdate();
	}
}
