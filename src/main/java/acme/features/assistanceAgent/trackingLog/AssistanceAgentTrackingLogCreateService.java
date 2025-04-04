
package acme.features.assistanceAgent.trackingLog;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claim.Claim;
import acme.entities.trackingLog.TrackingLog;
import acme.entities.trackingLog.TrackingLogIndicator;
import acme.realms.assistanceAgent.AssistanceAgent;

@GuiService
public class AssistanceAgentTrackingLogCreateService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	@Autowired
	AssistanceAgentTrackingLogRepository repository;


	@Override
	public void authorise() {
		boolean isAssistanceAgent = super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgent.class);
		int claimId = super.getRequest().getData("claimId", int.class);
		Claim claim = this.repository.findClaimById(claimId);
		int assistanceAgentId = this.repository.findAssistanceAgentIdByUserAccountId(super.getRequest().getPrincipal().getAccountId());
		boolean isClaimOwner = claim.getAssistanceAgent().getId() == assistanceAgentId;
		boolean status = claim.getPublished() && isAssistanceAgent && isClaimOwner;
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		TrackingLog trackingLog = new TrackingLog();
		trackingLog.setLastUpdateMoment(MomentHelper.getCurrentMoment());
		trackingLog.setPublished(false);
		int claimId = super.getRequest().getData("claimId", int.class);
		Claim claim = this.repository.findClaimById(claimId);
		trackingLog.setClaim(claim);
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
	}

	@Override
	public void perform(final TrackingLog trackingLog) {
		assert trackingLog != null;
		this.repository.save(trackingLog);
	}

	@Override
	public void unbind(final TrackingLog trackingLog) {
		Dataset dataset = super.unbindObject(trackingLog, "step", "resolutionPercentage", "indicator", "resolution");
		SelectChoices trackingLogIndicators = SelectChoices.from(TrackingLogIndicator.class, trackingLog.getIndicator());
		dataset.put("trackingLogIndicators", trackingLogIndicators);
		dataset.put("claimId", trackingLog.getClaim().getId());
		super.getResponse().addData(dataset);
	}

}
