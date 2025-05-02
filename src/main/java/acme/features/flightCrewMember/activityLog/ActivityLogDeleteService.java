
package acme.features.flightCrewMember.activityLog;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activityLog.ActivityLog;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiService
public class ActivityLogDeleteService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

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
		int id = request.getData("id", int.class);
		ActivityLog log = this.repository.findById(id);
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
		// Nothing to bind for delete
	}

	@Override
	public void validate(final ActivityLog log) {
		// No additional validation
	}

	@Override
	public void perform(final ActivityLog log) {
		this.repository.delete(log);
	}

	@Override
	public void unbind(final ActivityLog log) {
		Dataset data = super.unbindObject(log, "registrationMoment", "typeOfIncident", "description", "severityLevel");
		data.put("flightAssignmentIdentificator", log.getFlightAssignment().getIdentificator());
		data.put("fid", log.getFlightAssignment().getId());
		super.getResponse().addData(data);
	}

	@Override
	public void onSuccess() {
		if ("POST".equalsIgnoreCase(super.getRequest().getMethod()))
			PrincipalHelper.handleUpdate();
	}
}
