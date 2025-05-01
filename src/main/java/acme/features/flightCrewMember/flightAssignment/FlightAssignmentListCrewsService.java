
package acme.features.flightCrewMember.flightAssignment;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightAssignment.FlightAssignment;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiService
public class FlightAssignmentListCrewsService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private FlightAssignmentRepository repository;


	@Override
	public void authorise() {
		var request = super.getRequest();
		var principal = request.getPrincipal();

		// Must be FlightCrewMember and have Fid parameter
		if (!principal.hasRealmOfType(FlightCrewMember.class) || !request.hasData("Fid", int.class)) {
			super.getResponse().setAuthorised(false);
			return;
		}

		int fid = request.getData("Fid", int.class);
		FlightAssignment myAssignment = this.repository.findById(fid);

		// Assignment must exist, be CONFIRMED, belong to current user, and not be Lead Attendant
		int currentMember = principal.getActiveRealm().getId();
		boolean authorised = myAssignment != null && "CONFIRMED".equals(myAssignment.getCurrentStatus()) && myAssignment.getFlightCrewMember().getId() == currentMember && !"LEAD ATTENDANT".equals(myAssignment.getDuty());

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		int fid = super.getRequest().getData("Fid", int.class);
		int legId = this.repository.findLegOfFlightAssignment(fid);
		int currentMember = super.getRequest().getPrincipal().getActiveRealm().getId();

		Collection<FlightAssignment> crew = this.repository.findFlightAssignmentsOfLeg(legId).stream().filter(fa -> !"CANCELLED".equals(fa.getCurrentStatus())).filter(fa -> fa.getFlightCrewMember().getId() != currentMember)
			.filter(fa -> "CONFIRMED".equals(fa.getCurrentStatus())).collect(Collectors.toList());

		super.getBuffer().addData(crew);
	}

	@Override
	public void unbind(final FlightAssignment assignment) {
		Dataset data = super.unbindObject(assignment, "duty", "momentOfLastUpdate", "currentStatus");
		super.getResponse().addData(data);
	}

}
