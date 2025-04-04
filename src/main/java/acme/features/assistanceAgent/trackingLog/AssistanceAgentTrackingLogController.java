
package acme.features.assistanceAgent.trackingLog;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.entities.trackingLog.TrackingLog;
import acme.realms.assistanceAgent.AssistanceAgent;

@GuiController
public class AssistanceAgentTrackingLogController extends AbstractGuiController<AssistanceAgent, TrackingLog> {

	@Autowired
	AssistanceAgentTrackingLogListService		listService;

	@Autowired
	AssistanceAgentTrackingLogShowService		showService;

	@Autowired
	AssistanceAgentTrackingLogCreateService		createService;

	@Autowired
	AssistanceAgentTrackingLogUpdateService		updateService;

	@Autowired
	AssistanceAgentTrackingLogPublishService	publishService;

	@Autowired
	AssistanceAgentTrackingLogDeleteService		deleteService;


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("update", this.updateService);
		super.addCustomCommand("publish", "update", this.publishService);
		super.addBasicCommand("delete", this.deleteService);
	}

}
