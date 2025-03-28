package acme.features.flightCrewMember.flightAssignment;

import java.util.Collection;

import acme.entities.flightAssignment.FlightAssignment;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiService
public class FlightAssignmentDeleteService  extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

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

		Integer memberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		flightAssignments = this.repository.findAllMyPlannedFlightAssignments(memberId);

		super.getBuffer().addData(flightAssignments);
	}

	@Override
	public void unbind(final FlightAssignment flightAssignment) {
		Dataset dataset;
		dataset = super.unbindObject(flightAssignment, "duty", "momentOfLastUpdate", "currentStatus");

		super.getResponse().addData(dataset);
	}
    
}
