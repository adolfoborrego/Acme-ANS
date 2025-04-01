
package acme.features.assistanceAgent.claim;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.claim.Claim;
import acme.realms.assistanceAgent.AssistanceAgent;

@GuiController
public class AssistanceAgentClaimController extends AbstractGuiController<AssistanceAgent, Claim> {

	@Autowired
	private AssistanceAgentCompletedClaimListService	completedListService;

	@Autowired
	private AssistanceAgentPendingClaimListService		pendingListService;

	@Autowired
	private AssistanceAgentClaimShowService				showService;

	@Autowired
	private AssistanceAgentClaimCreateService			createService;

	@Autowired
	private AssistanceAgentClaimDeleteService			deleteService;

	@Autowired
	private AssistanceAgentClaimUpdateService			updateService;


	@PostConstruct
	protected void initialise() {
		super.addCustomCommand("completed-list", "list", this.completedListService);
		super.addCustomCommand("pending-list", "list", this.pendingListService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("delete", this.deleteService);
		super.addBasicCommand("update", this.updateService);
	}

}
