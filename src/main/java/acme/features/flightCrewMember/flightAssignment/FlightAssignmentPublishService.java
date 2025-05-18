
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

	@Autowired
	private FlightAssignmentRepository repository;


	@Override
	public void authorise() {
		// Must have request data and be a FlightCrewMember
		var principal = super.getRequest().getPrincipal();
		boolean hasId = super.getRequest().hasData("id", int.class);
		int id = super.getRequest().getData("id", int.class);
		FlightAssignment assignment = this.repository.findById(id);
		if (assignment != null) {
			super.getResponse().setAuthorised(!assignment.getCurrentStatus().equals("CONFIRMED"));
			return;
		}
		super.getResponse().setAuthorised(hasId && principal.hasRealmOfType(FlightCrewMember.class));
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		FlightAssignment assignment = this.repository.findById(id);
		if (assignment != null) {
			this.updateToConfirmed(assignment);
			super.getBuffer().addData(assignment);
		}
	}

	@Override
	public void bind(final FlightAssignment assignment) {
		super.bindObject(assignment);
	}

	@Override
	public void validate(final FlightAssignment assignment) {
		// No extra validation required
	}

	@Override
	public void perform(final FlightAssignment assignment) {
		this.updateToConfirmed(assignment);
		this.repository.save(assignment);
	}

	@Override
	public void onSuccess() {
		if ("POST".equalsIgnoreCase(super.getRequest().getMethod()))
			PrincipalHelper.handleUpdate();
	}

	// --- Helper ---
	private void updateToConfirmed(final FlightAssignment assignment) {
		assignment.setMomentOfLastUpdate(MomentHelper.getCurrentMoment());
		assignment.setCurrentStatus("CONFIRMED");
	}
}
