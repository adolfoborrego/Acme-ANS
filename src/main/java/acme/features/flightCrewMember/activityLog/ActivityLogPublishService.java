
package acme.features.flightCrewMember.activityLog;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activityLog.ActivityLog;
import acme.entities.leg.LegStatus;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiService
public class ActivityLogPublishService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	@Autowired
	private ActivityLogRepository repository;


	@Override
	public void authorise() {
		var request = super.getRequest();
		var principal = request.getPrincipal();
		// Must be FlightCrewMember and have log id
		if (!principal.hasRealmOfType(FlightCrewMember.class) || !request.hasData("id", int.class)) {
			super.getResponse().setAuthorised(false);
			return;
		}
		int logId = request.getData("id", int.class);
		ActivityLog log = this.repository.findById(logId);
		if (log == null) {
			super.getResponse().setAuthorised(false);
			return;
		}
		// Must belong to user and leg landed and not already published
		var assignment = log.getFlightAssignment();
		boolean ok = assignment.getFlightCrewMember().getId() == principal.getActiveRealm().getId() && assignment.getCurrentStatus().equals("CONFIRMED") && assignment.getLeg().getStatus() == LegStatus.LANDED && !log.getIsPublished();
		super.getResponse().setAuthorised(ok);
	}

	@Override
	public void load() {
		int logId = super.getRequest().getData("id", int.class);
		ActivityLog log = this.repository.findById(logId);
		super.getBuffer().addData(log);
	}

	@Override
	public void bind(final ActivityLog log) {
		// No editable fields
	}

	@Override
	public void validate(final ActivityLog log) {
		// No extra validation
	}

	@Override
	public void perform(final ActivityLog log) {
		log.setIsPublished(true);
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
