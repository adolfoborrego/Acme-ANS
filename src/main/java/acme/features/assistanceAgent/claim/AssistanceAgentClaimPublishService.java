
package acme.features.assistanceAgent.claim;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claim.Claim;
import acme.realms.assistanceAgent.AssistanceAgent;

@GuiService
public class AssistanceAgentClaimPublishService extends AbstractGuiService<AssistanceAgent, Claim> {

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
		boolean isPublished = claim.getPublished();
		boolean status = claim != null && isAssistanceAgent && isClaimOwner && !isPublished;
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
	}

	@Override
	public void perform(final Claim claim) {
		assert claim != null;
		claim.setPublished(true);
		this.repository.save(claim);
	}

	@Override
	public void unbind(final Claim claim) {
		assert claim != null;
	}

}
