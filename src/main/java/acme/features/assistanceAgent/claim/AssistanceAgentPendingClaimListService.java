
package acme.features.assistanceAgent.claim;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claim.Claim;
import acme.realms.assistanceAgent.AssistanceAgent;

@GuiService
public class AssistanceAgentPendingClaimListService extends AbstractGuiService<AssistanceAgent, Claim> {

	@Autowired
	private AssistanceAgentClaimRepository repository;


	@Override
	public void authorise() {
		boolean status = super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgent.class);
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int userAccountId = super.getRequest().getPrincipal().getAccountId();
		int assistanceAgentId = this.repository.findAssistanceAgentIdByUserAccountId(userAccountId);
		Collection<Claim> claims = this.repository.findPendingClaimsByAssistanceAgentId(assistanceAgentId);
		super.getBuffer().addData(claims);
	}

	@Override
	public void unbind(final Claim claim) {
		assert claim != null;
		Dataset dataset = super.unbindObject(claim, "registrationMoment", "passengerEmail", "description", "type", "published");
		dataset.put("indicator", claim.getIndicator());
		dataset.put("leg", claim.getLeg() == null ? "----" : claim.getLeg().getId());
		boolean showCreate = super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgent.class);
		super.getResponse().addData(dataset);
		super.getResponse().addGlobal("showCreate", showCreate);
	}

}
