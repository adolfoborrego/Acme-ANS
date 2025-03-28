
package acme.features.airlineManager.leg;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.leg.Leg;
import acme.realms.airlineManager.AirlineManager;

@GuiService
public class ManagerLegDeleteService extends AbstractGuiService<AirlineManager, Leg> {

	@Autowired
	protected ManagerLegRepository repository;


	@Override
	public void authorise() {
		int id = super.getRequest().getData("id", int.class);
		Leg leg = this.repository.findLegById(id);

		boolean authorised = leg != null && super.getRequest().getPrincipal().hasRealm(leg.getFlight().getAirlineManager()) && !leg.getPublished();

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void bind(final Leg leg) {
		// Nada que bindear
	}

	@Override
	public void validate(final Leg leg) {
		// Nada que validar
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		Leg leg = this.repository.findLegById(id);
		super.getBuffer().addData(leg);
	}

	@Override
	public void perform(final Leg leg) {
		this.repository.delete(leg);
	}
}
