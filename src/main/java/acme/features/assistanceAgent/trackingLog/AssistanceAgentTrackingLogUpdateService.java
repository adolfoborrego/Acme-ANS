
package acme.features.assistanceAgent.trackingLog;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.trackingLog.TrackingLog;
import acme.entities.trackingLog.TrackingLogIndicator;
import acme.realms.assistanceAgent.AssistanceAgent;

@GuiService
public class AssistanceAgentTrackingLogUpdateService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	@Autowired
	AssistanceAgentTrackingLogRepository repository;


	@Override
	public void authorise() {
		boolean isAssistanceAgent = super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgent.class);
		int trackingLogId = super.getRequest().getData("id", int.class);
		TrackingLog trackingLog = this.repository.findTrackingLogById(trackingLogId);
		boolean isTrackingLogOwner = false;
		boolean isPublished = true;
		if (isAssistanceAgent && trackingLog != null) {
			int userAccountId = super.getRequest().getPrincipal().getAccountId();
			int assistanceAgentId = this.repository.findAssistanceAgentIdByUserAccountId(userAccountId);
			isTrackingLogOwner = trackingLog.getClaim().getAssistanceAgent().getId() == assistanceAgentId;
			isPublished = trackingLog.getPublished();
		}
		boolean status = isTrackingLogOwner && !isPublished;
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int trackingLogId = super.getRequest().getData("id", int.class);
		TrackingLog trackingLog = this.repository.findTrackingLogById(trackingLogId);
		super.getBuffer().addData(trackingLog);
	}

	@Override
	public void bind(final TrackingLog trackingLog) {
		assert trackingLog != null;
		super.bindObject(trackingLog, "step", "resolutionPercentage", "indicator", "resolution");
	}

	@Override
	public void validate(final TrackingLog trackingLog) {
		assert trackingLog != null;
		final boolean isComplete = trackingLog.getResolutionPercentage() == 100;
		final TrackingLogIndicator indicator = trackingLog.getIndicator();
		if (!isComplete)
			super.state(indicator == TrackingLogIndicator.PENDING, "indicator", "assistance-agent.tracking-log.error.indicator-must-be-pending");
		if (isComplete) {
			boolean valid = indicator == TrackingLogIndicator.ACCEPTED || indicator == TrackingLogIndicator.REJECTED || indicator == TrackingLogIndicator.IN_REVIEW;
			super.state(valid, "indicator", "assistance-agent.tracking-log.error.indicator-must-be-accepted-or-rejected-or-in-review");
			boolean hasResolution = trackingLog.getResolution() != null && !trackingLog.getResolution().trim().isEmpty();
			super.state(hasResolution, "resolution", "assistance-agent.tracking-log.error.resolution-required-if-complete");
		}
		TrackingLog originalTrackingLog = this.repository.findTrackingLogById(trackingLog.getId());
		boolean isModified = !trackingLog.getStep().equals(originalTrackingLog.getStep()) || trackingLog.getResolutionPercentage() != originalTrackingLog.getResolutionPercentage() || trackingLog.getIndicator() != originalTrackingLog.getIndicator()
			|| trackingLog.getResolution() != null && !trackingLog.getResolution().equals(originalTrackingLog.getResolution()) || trackingLog.getResolution() == null && originalTrackingLog.getResolution() != null;
		super.state(isModified, "*", "assistance-agent.tracking-log.error.no-changes");
	}

	@Override
	public void perform(final TrackingLog trackingLog) {
		assert trackingLog != null;
		trackingLog.setLastUpdateMoment(MomentHelper.getCurrentMoment());
		this.repository.save(trackingLog);
	}

	@Override
	public void unbind(final TrackingLog trackingLog) {
		assert trackingLog != null;
		Dataset dataset = super.unbindObject(trackingLog, "lastUpdateMoment", "step", "resolutionPercentage", "indicator", "resolution", "published");
		SelectChoices trackingLogIndicators = SelectChoices.from(TrackingLogIndicator.class, trackingLog.getIndicator());
		dataset.put("trackingLogIndicators", trackingLogIndicators);
		dataset.put("claimId", trackingLog.getClaim().getId());
		super.getResponse().addData(dataset);
	}

}
