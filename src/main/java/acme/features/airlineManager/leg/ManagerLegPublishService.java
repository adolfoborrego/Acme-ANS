
package acme.features.airlineManager.leg;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.leg.Leg;
import acme.realms.airlineManager.AirlineManager;

@GuiService
public class ManagerLegPublishService extends AbstractGuiService<AirlineManager, Leg> {

	@Autowired
	protected ManagerLegRepository repository;


	@Override
	public void authorise() {
		int id = super.getRequest().getData("id", int.class);
		Leg leg = this.repository.findLegById(id);

		boolean authorised = leg != null && !leg.getPublished() && super.getRequest().getPrincipal().hasRealm(leg.getFlight().getAirlineManager());

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		Leg leg = this.repository.findLegById(id);
		super.getBuffer().addData(leg);
	}

	@Override
	public void bind(final Leg leg) {
		// No binding necesario para publicación
	}

	@Override
	public void validate(final Leg leg) {
		// Validación opcional
	}

	@Override
	public void perform(final Leg leg) {
		leg.setPublished(true);
		this.repository.save(leg);
	}

	@Override
	public void unbind(final Leg leg) {
		super.getResponse().addData(super.unbindObject(leg, "published"));
	}

	@Override
	public void onSuccess() {
		PrincipalHelper.handleUpdate();
	}
}
