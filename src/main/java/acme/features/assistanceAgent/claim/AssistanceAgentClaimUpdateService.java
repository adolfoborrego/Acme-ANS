
package acme.features.assistanceAgent.claim;

import java.util.Collection;
import java.util.Date;
import java.util.Objects;

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
public class AssistanceAgentClaimUpdateService extends AbstractGuiService<AssistanceAgent, Claim> {

	@Autowired
	protected AssistanceAgentClaimRepository repository;


	@Override
	public void authorise() {
		boolean isAssistanceAgent = super.getRequest().getPrincipal().hasRealmOfType(AssistanceAgent.class);
		int claimId = super.getRequest().getData("id", int.class);
		Claim claim = this.repository.findClaimById(claimId);
		boolean isClaimOwner = false;
		boolean isClaimPublished = true;
		if (isAssistanceAgent && claim != null) {
			int userAccountId = super.getRequest().getPrincipal().getAccountId();
			int assistanceAgentId = this.repository.findAssistanceAgentIdByUserAccountId(userAccountId);
			isClaimOwner = claim.getAssistanceAgent().getId() == assistanceAgentId;
			isClaimPublished = claim.getPublished();
		}
		boolean status = isClaimOwner && !isClaimPublished;
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
		super.bindObject(claim, "passengerEmail", "description", "type", "leg");
	}

	@Override
	public void validate(final Claim claim) {
		assert claim != null;
		Claim originalClaim = this.repository.findClaimById(claim.getId());
		super.state(Objects.equals(claim.getRegistrationMoment(), originalClaim.getRegistrationMoment()), "registrationMoment", "assistance-agent.claim.error.not-possible-to-modify-registrationMoment");
		super.state(Objects.equals(claim.getPublished(), originalClaim.getPublished()), "published", "assistance-agent.claim.error.not-possible-to-modify-published");
		super.state(claim.getLeg() != null, "leg", "assistance-agent.claim.error.no-leg");
		Date now = MomentHelper.getCurrentMoment();
		Collection<Leg> validLegs = this.repository.findFinishedLegs(now);
		boolean legIsValid = validLegs.stream().anyMatch(validLeg -> validLeg.getId() == claim.getLeg().getId());
		if (!legIsValid)
			throw new IllegalArgumentException("Attempted to use an invalid leg ID: possible tampering detected.");
		boolean isModified = !Objects.equals(claim.getPassengerEmail(), originalClaim.getPassengerEmail()) || !Objects.equals(claim.getDescription(), originalClaim.getDescription()) || !Objects.equals(claim.getType(), originalClaim.getType())
			|| !Objects.equals(claim.getLeg(), originalClaim.getLeg());
		super.state(isModified, "*", "assistance-agent.claim.error.no-changes");
	}

	@Override
	public void perform(final Claim claim) {
		assert claim != null;
		this.repository.save(claim);
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
