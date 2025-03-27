
package acme.features.assistanceAgent.claim;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claim.Claim;
import acme.realms.assistanceAgent.AssistanceAgent;

@GuiService
public class AssistanceAgentCompletedClaimListService extends AbstractGuiService<AssistanceAgent, Claim> {

	@Autowired
	private AssistanceAgentClaimRepository repository;


	@Override
	public void authorise() {
		boolean status;
		status = super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgent.class);
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Collection<Claim> claims;
		int userAccountId;
		int assistanceAgentId;
		userAccountId = super.getRequest().getPrincipal().getAccountId();
		assistanceAgentId = this.repository.findAssistanceAgentIdByUserAccountId(userAccountId);
		claims = this.repository.findCompletedClaimsByAssistanceAgentId(assistanceAgentId);
		super.getBuffer().addData(claims);
	}

	@Override
	public void unbind(final Claim claim) {
		assert claim != null;
		Dataset dataset;
		dataset = super.unbindObject(claim, "registrationMoment", "passengerEmail", "description", "type", "published");
		super.getResponse().addData(dataset);
	}

}
