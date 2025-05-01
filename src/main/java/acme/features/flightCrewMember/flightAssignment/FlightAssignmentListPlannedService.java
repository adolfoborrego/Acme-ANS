
package acme.features.flightCrewMember.flightAssignment;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightAssignment.FlightAssignment;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiService
public class FlightAssignmentListPlannedService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private FlightAssignmentRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		Boolean auth = true;
		if (!super.getRequest().getPrincipal().hasRealmOfType(FlightCrewMember.class))
			auth = false;
		super.getResponse().setAuthorised(auth);
	}

	@Override
	public void load() {
		Collection<FlightAssignment> flightAssignments;

		Integer memberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		Date currentDate = MomentHelper.getCurrentMoment();
		flightAssignments = this.repository.findAllMyPlannedFlightAssignments(memberId, currentDate);

		super.getBuffer().addData(flightAssignments);
	}

	@Override
	public void unbind(final FlightAssignment flightAssignment) {
		Dataset dataset;
		dataset = super.unbindObject(flightAssignment, "duty", "momentOfLastUpdate", "currentStatus");

		super.getResponse().addData(dataset);
	}

}
