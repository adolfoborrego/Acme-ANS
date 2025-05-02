
package acme.features.flightCrewMember.activityLog;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activityLog.ActivityLog;
import acme.entities.flightAssignment.FlightAssignment;
import acme.entities.leg.LegStatus;
import acme.features.flightCrewMember.flightAssignment.FlightAssignmentRepository;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiService
public class ActivityLogCreateService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	@Autowired
	private ActivityLogRepository		repository;

	@Autowired
	private FlightAssignmentRepository	assignmentRepository;


	@Override
	public void authorise() {
		var request = super.getRequest();
		var principal = request.getPrincipal();

		// Must be FlightCrewMember and have assignment id
		if (!principal.hasRealmOfType(FlightCrewMember.class) || !request.hasData("id", int.class)) {
			super.getResponse().setAuthorised(false);
			return;
		}

		int assignmentId = request.getData("id", int.class);
		Optional<FlightAssignment> opt = Optional.ofNullable(this.assignmentRepository.findById(assignmentId));

		boolean authorised = opt.map(a -> a.getLeg().getStatus() == LegStatus.LANDED && a.getFlightCrewMember().getId() == principal.getActiveRealm().getId()).orElse(false);

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		int assignmentId = super.getRequest().getData("id", int.class);
		FlightAssignment assignment = this.assignmentRepository.findById(assignmentId);

		ActivityLog log = new ActivityLog();
		log.setRegistrationMoment(MomentHelper.getCurrentMoment());
		log.setIsPublished(false);
		log.setFlightAssignment(assignment);

		super.getBuffer().addData(log);
	}

	@Override
	public void bind(final ActivityLog log) {
		assert log != null;
		super.bindObject(log, "typeOfIncident", "description", "severityLevel");
	}

	@Override
	public void validate(final ActivityLog log) {
		assert log != null;
		// Validation annotations cover required fields
	}

	@Override
	public void perform(final ActivityLog log) {
		log.setRegistrationMoment(MomentHelper.getCurrentMoment());
		log.setIsPublished(false);
		this.repository.save(log);
	}

	@Override
	public void unbind(final ActivityLog log) {
		Dataset data = super.unbindObject(log, "registrationMoment", "typeOfIncident", "description", "severityLevel", "isPublished");
		data.put("duty", log.getFlightAssignment().getDuty());
		data.put("leg", log.getFlightAssignment().getLeg().getIdentificator());
		data.put("fid", log.getFlightAssignment().getId());
		super.getResponse().addData(data);
	}

	@Override
	public void onSuccess() {
		if ("POST".equalsIgnoreCase(super.getRequest().getMethod()))
			PrincipalHelper.handleUpdate();
	}
}
