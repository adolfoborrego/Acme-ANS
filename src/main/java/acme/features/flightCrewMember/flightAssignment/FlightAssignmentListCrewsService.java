
package acme.features.flightCrewMember.flightAssignment;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightAssignment.FlightAssignment;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiService
public class FlightAssignmentListCrewsService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

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
		Collection<FlightAssignment> flightAssignments;

		Integer flightAssignmentId = super.getRequest().getData("Fid", Integer.class);
		Integer legId = this.repository.findLegOfFlightAssignment(flightAssignmentId);
		flightAssignments = this.repository.findFlightAssignmentsOfLeg(legId);

		Integer flightCrewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();

		flightAssignments = flightAssignments.stream().filter(x -> x.getFlightCrewMember().getId() != flightCrewMemberId).toList();

		super.getBuffer().addData(flightAssignments);
	}

	@Override
	public void unbind(final FlightAssignment flightAssignment) {
		Dataset dataset;
		dataset = super.unbindObject(flightAssignment, "duty", "momentOfLastUpdate", "currentStatus");
		super.getResponse().addData(dataset);
	}

}
