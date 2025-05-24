
package acme.features.assistanceAgent.claim;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.claim.Claim;
import acme.entities.claim.ClaimType;
import acme.entities.leg.Leg;
import acme.realms.assistanceAgent.AssistanceAgent;

@GuiService
public class AssistanceAgentClaimCreateService extends AbstractGuiService<AssistanceAgent, Claim> {

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
		Claim claim = new Claim();
		claim.setPublished(false);
		claim.setRegistrationMoment(MomentHelper.getCurrentMoment());
		super.getBuffer().addData(claim);
	}

	@Override
	public void bind(final Claim claim) {
		assert claim != null;
		super.bindObject(claim, "passengerEmail", "description", "type", "leg");
	}

	@Override
	public void validate(final Claim claim) {
		assert claim != null;
		if (claim.getLeg() == null)
			super.state(false, "leg", "assistance-agent.claim.error.no-leg");
		Date now = MomentHelper.getCurrentMoment();
		Collection<Leg> validLegs = this.repository.findFinishedLegs(now);
		boolean legIsValid = validLegs.stream().anyMatch(validLeg -> validLeg.getId() == claim.getLeg().getId());
		if (!legIsValid)
			throw new IllegalArgumentException("Attempted to use an invalid leg ID: possible tampering detected.");
	}

	@Override
	public void perform(final Claim claim) {
		assert claim != null;
		int userAccountId = super.getRequest().getPrincipal().getAccountId();
		int asssitanceAgentId = this.repository.findAssistanceAgentIdByUserAccountId(userAccountId);
		AssistanceAgent assistanceAgent = new AssistanceAgent();
		assistanceAgent.setId(asssitanceAgentId);
		claim.setAssistanceAgent(assistanceAgent);
		this.repository.save(claim);
	}

	@Override
	public void unbind(final Claim claim) {
		Dataset dataset;
		dataset = super.unbindObject(claim, "passengerEmail", "description", "type", "leg");
		SelectChoices claimTypes = SelectChoices.from(ClaimType.class, claim.getType());
		dataset.put("claimTypes", claimTypes);
		Date now = MomentHelper.getCurrentMoment();
		SelectChoices legs = SelectChoices.from(this.repository.findFinishedLegs(now), "id", claim.getLeg());
		dataset.put("legs", legs);
		super.getResponse().addData(dataset);
	}

}
