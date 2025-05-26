
package acme.features.flightCrewMember.activityLog;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activityLog.ActivityLog;
import acme.entities.flightAssignment.FlightAssignment;
import acme.entities.leg.LegStatus;
import acme.features.flightCrewMember.flightAssignment.FlightAssignmentRepository;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiService
public class ActivityLogListService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	@Autowired
	private ActivityLogRepository		repository;

	@Autowired
	private FlightAssignmentRepository	assignmentRepository;


	@Override
	public void authorise() {
		var request = super.getRequest();
		var principal = request.getPrincipal();

		// Must be FlightCrewMember
		if (!principal.hasRealmOfType(FlightCrewMember.class)) {
			super.getResponse().setAuthorised(false);
			return;
		}

		// Must reference a FlightAssignment belonging to current user
		if (!request.hasData("id", int.class)) {
			super.getResponse().setAuthorised(false);
			return;
		}

		int assignmentId = request.getData("id", int.class);
		FlightAssignment assignment = this.assignmentRepository.findById(assignmentId);
		if (assignment == null || !assignment.getLeg().getStatus().equals(LegStatus.LANDED) || assignment.getFlightCrewMember().getId() != principal.getActiveRealm().getId()) {
			super.getResponse().setAuthorised(false);
			return;
		}

		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		int flightAssignmentId = super.getRequest().getData("id", int.class);
		Collection<ActivityLog> activityLogs = this.repository.findActivityLogsOf(flightAssignmentId);
		super.getBuffer().addData(activityLogs);
	}

	@Override
	public void unbind(final ActivityLog activityLog) {
		Dataset dataset = super.unbindObject(activityLog, "registrationMoment", "typeOfIncident", "severityLevel");
		super.getResponse().addData(dataset);
	}
}
