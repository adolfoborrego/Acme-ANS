
package acme.features.assistanceAgent.trackingLog;

import java.util.List;

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
		boolean isClaimOwner = false;
		boolean isClaimPublished = false;

		if (isAssistanceAgent && claim != null) {
			int userAccountId = super.getRequest().getPrincipal().getAccountId();
			int assistanceAgentId = this.repository.findAssistanceAgentIdByUserAccountId(userAccountId);
			isClaimOwner = claim.getAssistanceAgent().getId() == assistanceAgentId;
			isClaimPublished = claim.getPublished();
		}

		boolean isClaimInReview = claim.getIndicator() == TrackingLogIndicator.IN_REVIEW;

		boolean isIndicatorLegal = true;
		boolean isPost = super.getRequest().getMethod().equalsIgnoreCase("POST");

		if (isPost && super.getRequest().hasData("indicator")) {
			String raw = super.getRequest().getData("indicator", String.class);
			TrackingLogIndicator indicator = "0".equals(raw) || raw == null ? null : TrackingLogIndicator.valueOf(raw);
			List<TrackingLogIndicator> legalIndicators = AssistanceAgentTrackingLogAuxiliary.getLegalIndicators(claim);
			isIndicatorLegal = legalIndicators.contains(indicator);
		}

		boolean status = isClaimOwner && isClaimPublished && !isClaimInReview && isIndicatorLegal;
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
		Double percentage = trackingLog.getResolutionPercentage();
		TrackingLogIndicator indicator = trackingLog.getIndicator();
		if (percentage < 100)
			super.state(indicator == TrackingLogIndicator.PENDING, "indicator", "assistance-agent.tracking-log.error.indicator-must-be-pending");
		else {
			boolean valid = indicator == TrackingLogIndicator.ACCEPTED || indicator == TrackingLogIndicator.REJECTED || indicator == TrackingLogIndicator.IN_REVIEW;
			super.state(valid, "indicator", "assistance-agent.tracking-log.error.indicator-must-not-be-pending");
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
		SelectChoices trackingLogIndicators = AssistanceAgentTrackingLogAuxiliary.getPossibleIndicatorChoices(trackingLog.getClaim(), trackingLog.getIndicator(), true);
		dataset.put("trackingLogIndicators", trackingLogIndicators);
		dataset.put("claimId", trackingLog.getClaim().getId());
		super.getResponse().addData(dataset);
	}

}
