
package acme.features.assistanceAgent.trackingLog;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.trackingLog.TrackingLog;
import acme.entities.trackingLog.TrackingLogIndicator;
import acme.realms.assistanceAgent.AssistanceAgent;

@GuiService
public class AssistanceAgentTrackingLogShowService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	@Autowired
	AssistanceAgentTrackingLogRepository repository;


	@Override
	public void authorise() {
		boolean isAssistanceAgent = super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgent.class);
		int trackingLogId = super.getRequest().getData("id", int.class);
		TrackingLog trackingLog = this.repository.findTrackingLogById(trackingLogId);
		boolean isTrackingLogOwner = false;
		if (isAssistanceAgent && trackingLog != null) {
			int userAccountId = super.getRequest().getPrincipal().getAccountId();
			int assistanceAgentId = this.repository.findAssistanceAgentIdByUserAccountId(userAccountId);
			isTrackingLogOwner = trackingLog.getClaim().getAssistanceAgent().getId() == assistanceAgentId;
		}
		boolean status = isTrackingLogOwner;
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int trackingLogId = super.getRequest().getData("id", int.class);
		TrackingLog trackingLog = this.repository.findTrackingLogById(trackingLogId);
		super.getBuffer().addData(trackingLog);
	}

	@Override
	public void unbind(final TrackingLog trackingLog) {
		assert trackingLog != null;
		Dataset dataset = super.unbindObject(trackingLog, "lastUpdateMoment", "step", "resolutionPercentage", "indicator", "resolution", "published");
		SelectChoices trackingLogIndicators = AssistanceAgentTrackingLogAuxiliary.getPossibleIndicatorChoices(trackingLog.getClaim(), trackingLog.getIndicator(), false);
		dataset.put("trackingLogIndicators", trackingLogIndicators);
		super.getResponse().addData(dataset);
		boolean showUpdateOrPublish = !(trackingLog.getClaim().getIndicator() == TrackingLogIndicator.IN_REVIEW);
		super.getResponse().addGlobal("showUpdateOrPublish", showUpdateOrPublish);
	}

}
