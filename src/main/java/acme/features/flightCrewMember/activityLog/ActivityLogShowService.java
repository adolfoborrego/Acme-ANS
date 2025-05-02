
package acme.features.flightCrewMember.activityLog;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activityLog.ActivityLog;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiService
public class ActivityLogShowService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	@Autowired
	private ActivityLogRepository repository;


	@Override
	public void authorise() {
		var request = super.getRequest();
		var principal = request.getPrincipal();
		if (!principal.hasRealmOfType(FlightCrewMember.class) || !request.hasData("id", int.class)) {
			super.getResponse().setAuthorised(false);
			return;
		}
		int logId = request.getData("id", int.class);
		ActivityLog log = this.repository.findById(logId);
		boolean authorised = log != null && log.getFlightAssignment().getFlightCrewMember().getId() == principal.getActiveRealm().getId();
		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		ActivityLog log = this.repository.findById(id);
		super.getBuffer().addData(log);
	}

	@Override
	public void bind(final ActivityLog log) {
		// No binding
	}

	@Override
	public void validate(final ActivityLog log) {
		// No validation
	}

	@Override
	public void perform(final ActivityLog log) {
		// No perform
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
		// No post-processing
	}
}
