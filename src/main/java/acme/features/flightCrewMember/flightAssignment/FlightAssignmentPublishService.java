
package acme.features.flightCrewMember.flightAssignment;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.helpers.MomentHelper;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightAssignment.FlightAssignment;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiService
public class FlightAssignmentPublishService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private FlightAssignmentRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void bind(final FlightAssignment object) {
		assert object != null;
		super.bindObject(object);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		FlightAssignment object = this.repository.findById(id);
		object.setMomentOfLastUpdate(MomentHelper.getCurrentMoment());
		object.setCurrentStatus("CONFIRMED");
		super.getBuffer().addData(object);
	}

	@Override
	public void validate(final FlightAssignment object) {
		assert object != null;
		// Aquí se podrían agregar validaciones adicionales si fuese necesario.
	}

	@Override
	public void perform(final FlightAssignment object) {

		// Actualización de datos básicos
		object.setMomentOfLastUpdate(MomentHelper.getCurrentMoment());
		object.setCurrentStatus("CONFIRMED");

		this.repository.save(object);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equalsIgnoreCase("POST"))
			PrincipalHelper.handleUpdate();
	}

}
