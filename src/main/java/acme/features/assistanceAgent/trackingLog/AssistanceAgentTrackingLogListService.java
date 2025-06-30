
package acme.features.assistanceAgent.trackingLog;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claim.Claim;
import acme.entities.trackingLog.TrackingLog;
import acme.entities.trackingLog.TrackingLogIndicator;
import acme.realms.assistanceAgent.AssistanceAgent;

@GuiService
public class AssistanceAgentTrackingLogListService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

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
		boolean status = isClaimOwner && isClaimPublished;
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int claimId = super.getRequest().getData("claimId", int.class);
		Collection<TrackingLog> trackingLogs = this.repository.findTrackingLogsByClaimId(claimId);
		super.getBuffer().addData(trackingLogs);
		super.getResponse().addGlobal("claimId", claimId);
		Claim claim = this.repository.findClaimById(claimId);
		boolean showCreate = !(claim.getIndicator() == TrackingLogIndicator.IN_REVIEW);
		super.getResponse().addGlobal("showCreate", showCreate);
	}

	@Override
	public void unbind(final TrackingLog trackingLog) {
		assert trackingLog != null;
		Dataset dataset = super.unbindObject(trackingLog, "lastUpdateMoment", "step", "resolutionPercentage", "indicator", "resolution", "published");
		int claimId = super.getRequest().getData("claimId", int.class);
		dataset.put("claimId", claimId);
		super.getResponse().addData(dataset);
	}

}
