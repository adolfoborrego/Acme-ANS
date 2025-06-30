
package acme.features.assistanceAgent.trackingLog;

import java.util.List;
import java.util.Objects;

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

		boolean isClaimInReview = trackingLog.getClaim().getIndicator() == TrackingLogIndicator.IN_REVIEW;

		boolean isIndicatorLegal = true;
		boolean isPost = super.getRequest().getMethod().equalsIgnoreCase("POST");

		if (isPost && super.getRequest().hasData("indicator")) {
			String raw = super.getRequest().getData("indicator", String.class);
			TrackingLogIndicator indicator = "0".equals(raw) || raw == null ? null : TrackingLogIndicator.valueOf(raw);
			List<TrackingLogIndicator> legalIndicators = AssistanceAgentTrackingLogAuxiliary.getLegalIndicators(trackingLog.getClaim());
			legalIndicators.add(trackingLog.getIndicator());
			isIndicatorLegal = legalIndicators.contains(indicator);
		}

		boolean status = isTrackingLogOwner && !isPublished && !isClaimInReview && isIndicatorLegal;
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
		TrackingLog originalTrackingLog = this.repository.findTrackingLogById(trackingLog.getId());
		boolean isModified = !Objects.equals(trackingLog.getStep(), originalTrackingLog.getStep()) || !Objects.equals(trackingLog.getResolutionPercentage(), originalTrackingLog.getResolutionPercentage())
			|| !Objects.equals(trackingLog.getIndicator(), originalTrackingLog.getIndicator()) || !Objects.equals(trackingLog.getResolution(), originalTrackingLog.getResolution());
		super.state(isModified, "*", "assistance-agent.tracking-log.error.no-changes");

		Double percentage = trackingLog.getResolutionPercentage();
		TrackingLogIndicator indicator = trackingLog.getIndicator();
		if (percentage < 100)
			super.state(indicator == TrackingLogIndicator.PENDING, "indicator", "assistance-agent.tracking-log.error.indicator-must-be-pending");
		else {
			boolean valid = indicator == TrackingLogIndicator.ACCEPTED || indicator == TrackingLogIndicator.REJECTED || indicator == TrackingLogIndicator.IN_REVIEW;
			super.state(valid, "indicator", "assistance-agent.tracking-log.error.indicator-must-be-accepted-or-rejected-or-in-review");
			boolean hasResolution = trackingLog.getResolution() != null && !trackingLog.getResolution().trim().isEmpty();
			super.state(hasResolution, "resolution", "assistance-agent.tracking-log.error.resolution-required-if-complete");
		}

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
		SelectChoices trackingLogIndicators = AssistanceAgentTrackingLogAuxiliary.getPossibleIndicatorChoices(trackingLog.getClaim(), trackingLog.getIndicator(), false);
		dataset.put("trackingLogIndicators", trackingLogIndicators);
		dataset.put("claimId", trackingLog.getClaim().getId());
		boolean showUpdateOrPublish = !(trackingLog.getClaim().getIndicator() == TrackingLogIndicator.IN_REVIEW);
		super.getResponse().addGlobal("showUpdateOrPublish", showUpdateOrPublish);
		super.getResponse().addData(dataset);
	}

}
