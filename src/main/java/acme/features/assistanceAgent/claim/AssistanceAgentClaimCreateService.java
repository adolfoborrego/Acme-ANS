
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
		Claim object = new Claim();
		object.setPublished(false);
		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final Claim object) {
		assert object != null;
		super.bindObject(object, "registrationMoment", "passengerEmail", "description", "type", "leg");
	}

	@Override
	public void validate(final Claim object) {
		assert object != null;
	}

	@Override
	public void perform(final Claim object) {
		assert object != null;
		int userAccountId = super.getRequest().getPrincipal().getAccountId();
		int asssitanceAgentId = this.repository.findAssistanceAgentIdByUserAccountId(userAccountId);
		AssistanceAgent assistanceAgent = new AssistanceAgent();
		assistanceAgent.setId(asssitanceAgentId);
		object.setAssistanceAgent(assistanceAgent);
		this.repository.save(object);
	}

	@Override
	public void unbind(final Claim object) {
		Dataset dataset;
		dataset = super.unbindObject(object, "registrationMoment", "passengerEmail", "description", "type", "leg");
		SelectChoices claimTypes = SelectChoices.from(ClaimType.class, object.getType());
		dataset.put("claimTypes", claimTypes);
		SelectChoices legs = SelectChoices.from(this.repository.findAllLegs(), "id", object.getLeg());
		dataset.put("legs", legs);
		super.getResponse().addData(dataset);
	}

}
