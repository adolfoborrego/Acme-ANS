
package acme.features.assistanceAgent.claim;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claim.Claim;
import acme.entities.claim.ClaimType;
import acme.realms.assistanceAgent.AssistanceAgent;

@GuiService
public class AssistanceAgentClaimDeleteService extends AbstractGuiService<AssistanceAgent, Claim> {

	@Autowired
	protected AssistanceAgentClaimRepository repository;


	@Override
	public void authorise() {
		int claimId = super.getRequest().getData("id", int.class);
		boolean isAssistanceAgent = super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgent.class);
		int userAccountId = super.getRequest().getPrincipal().getAccountId();
		int assistanceAgentId = this.repository.findAssistanceAgentIdByUserAccountId(userAccountId);
		int ownerId = this.repository.findAssistanceAgentIdByClaimId(claimId);
		boolean isClaimOwner = assistanceAgentId == ownerId;
		Claim claim = this.repository.findClaimById(claimId);
		boolean status = claim != null && isAssistanceAgent && isClaimOwner;
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int claimId = super.getRequest().getData("id", int.class);
		Claim claim = this.repository.findClaimById(claimId);
		super.getBuffer().addData(claim);
	}

	@Override
	public void bind(final Claim claim) {
		assert claim != null;
	}

	@Override
	public void validate(final Claim claim) {
		assert claim != null;
		if (claim.getPublished())
			throw new IllegalArgumentException("Attempted to delete a pusblished Claim: possible tampering detected.");
	}

	@Override
	public void perform(final Claim claim) {
		assert claim != null;
		this.repository.delete(claim);
	}

	@Override
	public void unbind(final Claim claim) {
		assert claim != null;
		Dataset dataset;
		dataset = super.unbindObject(claim, "registrationMoment", "passengerEmail", "description", "type", "published");
		dataset.put("indicator", claim.getIndicator());
		SelectChoices claimTypes = SelectChoices.from(ClaimType.class, claim.getType());
		dataset.put("claimTypes", claimTypes);
		Date now = MomentHelper.getCurrentMoment();
		SelectChoices legs = SelectChoices.from(this.repository.findFinishedLegs(now), "id", claim.getLeg());
		dataset.put("legs", legs);
		super.getResponse().addData(dataset);
	}

}
