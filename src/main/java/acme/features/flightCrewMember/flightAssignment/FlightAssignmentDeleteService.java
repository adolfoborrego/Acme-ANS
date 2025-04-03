
package acme.features.flightCrewMember.flightAssignment;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.helpers.MomentHelper;
import acme.client.helpers.PrincipalHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightAssignment.FlightAssignment;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiService
public class FlightAssignmentDeleteService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private FlightAssignmentRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		FlightAssignment object = this.repository.findById(id);
		object.setMomentOfLastUpdate(MomentHelper.getCurrentMoment());
		object.setCurrentStatus("CANCELLED");
		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final FlightAssignment object) {
		assert object != null;
		super.bindObject(object, "leg");
	}

	@Override
	public void validate(final FlightAssignment object) {
		assert object != null;
		// Aquí se podrían agregar validaciones adicionales si fuese necesario.
	}

	@Override
	public void perform(FlightAssignment object) {
		assert object != null;

		// Actualización de datos básicos
		int id = super.getRequest().getData("id", int.class);

		object = this.repository.findById(id);
		object.setMomentOfLastUpdate(MomentHelper.getCurrentMoment());
		object.setCurrentStatus("CANCELLED");

		// Actualizar leg y remarks a partir de los datos recibidos
		Integer legId = super.getRequest().getData("leg", int.class);
		Collection<FlightAssignment> assignments = this.repository.findFlightAssignmentsOfLeg(legId);
		boolean deleteAll = false;
		for (FlightAssignment a : assignments)
			if (a.getId() == id) {
				if (a.getDuty().equals("LEAD ATTENDANT"))
					deleteAll = true;
				this.repository.save(a);
			}

		this.repository.save(object);

		if (deleteAll)
			for (FlightAssignment a : assignments) {
				a.setCurrentStatus("CANCELLED");
				this.repository.save(a);
			}
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equalsIgnoreCase("POST"))
			PrincipalHelper.handleUpdate();
	}

}
