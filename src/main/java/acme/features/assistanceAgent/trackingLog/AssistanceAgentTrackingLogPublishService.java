
package acme.features.assistanceAgent.trackingLog;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.trackingLog.TrackingLog;
import acme.entities.trackingLog.TrackingLogIndicator;
import acme.realms.assistanceAgent.AssistanceAgent;

@GuiService
public class AssistanceAgentTrackingLogPublishService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

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
		boolean isClaimInReview = trackingLog.getClaim().getIndicator() == TrackingLogIndicator.IN_REVIEW;
		boolean status = isTrackingLogOwner && !isPublished && !isClaimInReview;
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
	}

	@Override
	public void validate(final TrackingLog trackingLog) {
		assert trackingLog != null;
		Double resolutionPercentage = trackingLog.getResolutionPercentage();
		TrackingLogIndicator indicator = trackingLog.getIndicator();
		List<TrackingLog> trackingLogs = this.repository.findPublishedTrackingLogsByClaimId(trackingLog.getClaim().getId());
		if (!trackingLogs.isEmpty()) {
			TrackingLog last = trackingLogs.get(trackingLogs.size() - 1);
			if (last.getIndicator() == TrackingLogIndicator.PENDING) {
				boolean isIncreasing = resolutionPercentage > last.getResolutionPercentage();
				super.state(isIncreasing, "resolutionPercentage", "assistance-agent.tracking-log.resolution-percentage-must-increase");
				boolean isPendingOrAcceptedOrRejected = indicator == TrackingLogIndicator.PENDING || indicator == TrackingLogIndicator.ACCEPTED || indicator == TrackingLogIndicator.REJECTED;
				super.state(isPendingOrAcceptedOrRejected, "indicator", "assistance-agent.tracking-log.indicator-must-be-pending-or-accepted-or-rejected");

			} else if (last.getIndicator() == TrackingLogIndicator.ACCEPTED || last.getIndicator() == TrackingLogIndicator.REJECTED) {
				boolean isInReview = indicator == TrackingLogIndicator.IN_REVIEW;
				super.state(isInReview, "indicator", "assistance-agent.tracking-log.indicator-must-be-in-review");

			}
		}
	}

	@Override
	public void perform(final TrackingLog trackingLog) {
		assert trackingLog != null;
		trackingLog.setPublished(true);
		this.repository.save(trackingLog);
	}

	@Override
	public void unbind(final TrackingLog trackingLog) {
		assert trackingLog != null;
		Dataset dataset = super.unbindObject(trackingLog, "lastUpdateMoment", "step", "resolutionPercentage", "indicator", "resolution", "published");
		SelectChoices trackingLogIndicators = AssistanceAgentTrackingLogAuxiliary.getPossibleIndicatorChoices(trackingLog.getClaim(), trackingLog.getIndicator(), false);
		dataset.put("trackingLogIndicators", trackingLogIndicators);
		dataset.put("claimId", trackingLog.getClaim().getId());
		boolean showUpdateOrPublish = !(trackingLog.getClaim().getIndicator() == TrackingLogIndicator.IN_REVIEW);
		super.getResponse().addGlobal("showUpdateOrPublish", showUpdateOrPublish);
		super.getResponse().addData(dataset);
	}

}
