
package acme.features.flightCrewMember.activityLog;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activityLog.ActivityLog;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiService
public class ActivityLogUpdateService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	@Autowired
	private ActivityLogRepository repository;


	@Override
	public void authorise() {
		var request = super.getRequest();
		var principal = request.getPrincipal();

		if (super.getRequest().getMethod().equals("GET"))
			super.state(false, "*", "flight-crew-member.flight-assignment.error");

		// Must be FlightCrewMember and have log id
		if (!principal.hasRealmOfType(FlightCrewMember.class) || !request.hasData("id", int.class)) {
			super.getResponse().setAuthorised(false);
			return;
		}
		int logId = request.getData("id", int.class);
		ActivityLog log = this.repository.findById(logId);

		// Must belong to user and not published
		boolean authorised = log != null && log.getFlightAssignment().getFlightCrewMember().getId() == principal.getActiveRealm().getId() && !log.getIsPublished();
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
		super.bindObject(log, "typeOfIncident", "description", "severityLevel");
	}

	@Override
	public void validate(final ActivityLog log) {
		// No additional validation beyond annotations
		if (super.getRequest().getCommand().equals("update")) {
			ActivityLog original = this.repository.findById(log.getId());

			boolean isModified = !Objects.equals(log.getTypeOfIncident(), original.getTypeOfIncident()) || !Objects.equals(log.getDescription(), original.getDescription()) || !Objects.equals(log.getSeverityLevel(), original.getSeverityLevel());

			super.state(isModified, "*", "flight-crew-member.flight-assignment.error");
		}
	}

	@Override
	public void perform(final ActivityLog log) {
		// Keep registrationMoment and isPublished unchanged
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
		if (super.getRequest().getMethod().equalsIgnoreCase("POST"))
			PrincipalHelper.handleUpdate();
	}
}
