
package acme.features.flightCrewMember.flightAssignment;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightAssignment.FlightAssignment;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiService
public class FlightAssignmentListLeadAttendantCrewsService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private FlightAssignmentRepository repository;


	@Override
	public void authorise() {

		var request = super.getRequest();
		var principal = request.getPrincipal();

		if (!principal.hasRealmOfType(FlightCrewMember.class) || !request.hasData("Fid", int.class)) {
			super.getResponse().setAuthorised(false);
			return;
		}

		int fid = request.getData("Fid", int.class);
		FlightAssignment assignment = this.repository.findById(fid);

		if (assignment == null || !"CONFIRMED".equals(assignment.getCurrentStatus()) || !this.isCurrentUserLead(assignment)) {
			super.getResponse().setAuthorised(false);
			return;
		}

		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		int assignmentId = super.getRequest().getData("Fid", int.class);
		int legId = this.repository.findLegOfFlightAssignment(assignmentId);

		int currentMember = super.getRequest().getPrincipal().getActiveRealm().getId();
		Collection<FlightAssignment> crew = this.repository.findFlightAssignmentsOfLeg(legId).stream().filter(fa -> !"CANCELLED".equals(fa.getCurrentStatus())).filter(fa -> fa.getFlightCrewMember().getId() != currentMember).collect(Collectors.toList());

		super.getBuffer().addData(crew);
	}

	@Override
	public void unbind(final FlightAssignment assignment) {
		Dataset data = super.unbindObject(assignment, "duty", "momentOfLastUpdate", "currentStatus");
		data.put("showAddAssignment", this.isCurrentUserLead(this.repository.findById(assignment.getId())));
		super.getResponse().addData(data);
	}

	// --- Helpers ---

	private boolean isCurrentUserLead(final FlightAssignment assignment) {
		int currentMember = super.getRequest().getPrincipal().getActiveRealm().getId();
		return this.repository.findFlightAssignmentsOfLeg(assignment.getLeg().getId()).stream().filter(fa -> !"CANCELLED".equals(fa.getCurrentStatus())).filter(fa -> "LEAD ATTENDANT".equals(fa.getDuty()))
			.anyMatch(fa -> fa.getFlightCrewMember().getId() == currentMember);
	}
}
