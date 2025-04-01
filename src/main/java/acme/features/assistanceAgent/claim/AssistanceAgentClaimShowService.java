
package acme.features.assistanceAgent.claim;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claim.Claim;
import acme.entities.claim.ClaimType;
import acme.realms.assistanceAgent.AssistanceAgent;

@GuiService
public class AssistanceAgentClaimShowService extends AbstractGuiService<AssistanceAgent, Claim> {

	@Autowired
	private AssistanceAgentClaimRepository repository;


	@Override
	public void authorise() {
		boolean authorise;
		int claimId;
		int userAccountId;
		int assistanceAgentId;
		int ownerId;
		Claim claim;
		boolean isAssistanceAgent;
		boolean isClaimOwner;

		claimId = super.getRequest().getData("id", int.class);

		isAssistanceAgent = super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgent.class);

		userAccountId = super.getRequest().getPrincipal().getAccountId();
		assistanceAgentId = this.repository.findAssistanceAgentIdByUserAccountId(userAccountId);
		ownerId = this.repository.findAssistanceAgentIdByClaimId(claimId);
		isClaimOwner = assistanceAgentId == ownerId;

		claim = this.repository.findClaimById(claimId);

		authorise = claim != null && isAssistanceAgent && isClaimOwner;
		super.getResponse().setAuthorised(authorise);
	}

	@Override
	public void load() {
		int claimId;
		Claim claim;
		claimId = super.getRequest().getData("id", int.class);
		claim = this.repository.findClaimById(claimId);
		super.getBuffer().addData(claim);
	}

	@Override
	public void unbind(final Claim claim) {
		assert claim != null;
		Dataset dataset;
		dataset = super.unbindObject(claim, "registrationMoment", "passengerEmail", "description", "type", "published");
		dataset.put("indicator", claim.getIndicator());
		SelectChoices claimTypes = SelectChoices.from(ClaimType.class, claim.getType());
		dataset.put("claimTypes", claimTypes);
		SelectChoices legs = SelectChoices.from(this.repository.findAllLegs(), "id", claim.getLeg());
		dataset.put("legs", legs);
		super.getResponse().addData(dataset);
	}

}
