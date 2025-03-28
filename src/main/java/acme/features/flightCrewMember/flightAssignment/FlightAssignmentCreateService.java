package acme.features.flightCrewMember.flightAssignment;

import java.util.Collection;

import acme.entities.flightAssignment.FlightAssignment;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiService
public class FlightAssignmentCreateService  extends AbstractGuiService<FlightCrewMember, FlightAssignment>  {
    
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
		

		//super.getBuffer().addData();
	}

	@Override
	public void unbind(final FlightAssignment flightAssignment) {
		Dataset dataset;
		dataset = super.unbindObject(flightAssignment, "duty", "momentOfLastUpdate", "currentStatus");

		super.getResponse().addData(dataset);
	}
}
