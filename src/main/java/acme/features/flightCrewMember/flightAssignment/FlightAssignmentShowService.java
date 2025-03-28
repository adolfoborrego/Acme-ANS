
package acme.features.flightCrewMember.flightAssignment;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightAssignment.FlightAssignment;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiService
public class FlightAssignmentShowService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {
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
		FlightAssignment flightAssignment;

		int flighAssignmentId = super.getRequest().getData("id", int.class);
		flightAssignment = this.repository.findById(flighAssignmentId);

		super.getBuffer().addData(flightAssignment);
	}

	@Override
	public void unbind(final FlightAssignment flightAssignment) {
		Dataset dataset;
		Integer flighAssignmentId = super.getRequest().getData("id", int.class);
		Integer legId = this.repository.findLegOfFlightAssignment(flighAssignmentId);

		Collection<FlightCrewMember> flightCrewMembers = this.repository.findFlightCrewMembersOfLeg(legId);

		dataset = super.unbindObject(flightAssignment, "duty", "momentOfLastUpdate", "currentStatus", "leg", "remarks");

		dataset.put("flightCrewMembers", flightCrewMembers);
		Boolean readOnly;
		if (flightAssignment.getDuty().equals("LEAD ATTENDANT")){
			readOnly = false;
		}else{
			readOnly = true;
		}
		dataset.put(readOnly, "readOnly");

		super.getResponse().addData(dataset);


	}
}
