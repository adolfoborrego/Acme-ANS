
package acme.features.flightCrewMember.flightAssignment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightAssignment.FlightAssignment;
import acme.entities.flightAssignment.FlightAssignmentDuty;
import acme.entities.flightAssignment.FlightAssignmentStatus;
import acme.entities.leg.Leg;
import acme.realms.flightCrewMember.FlightCrewMember;

@GuiService
public class FlightAssignmentShowService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private FlightAssignmentRepository repository;


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		FlightCrewMember flightCrewMember = this.getFlightCrewMember();
		FlightAssignment flightAssignment = this.getFlightAssignment(flightCrewMember);
		super.getBuffer().addData(flightAssignment);
	}

	private FlightCrewMember getFlightCrewMember() {
		Integer memberId = super.getRequest().getPrincipal().getActiveRealm().getId();
		return this.repository.findFlightCrewMemberById(memberId);
	}

	private FlightAssignment getFlightAssignment(final FlightCrewMember flightCrewMember) {
		int flightAssignmentId = super.getRequest().getData("id", int.class);
		return this.repository.findById(flightAssignmentId);
	}

	private boolean isShowCommand() {
		return super.getRequest().getCommand().equals("show");
	}

	@Override
	public void unbind(final FlightAssignment flightAssignment) {
		Dataset dataset = super.unbindObject(flightAssignment, "momentOfLastUpdate", "currentStatus", "leg", "remarks");

		Collection<FlightAssignment> flightAssignments = this.getFlightAssignments(flightAssignment);
		dataset.put("duty", this.getDutySelectChoice(flightAssignment));
		dataset.put("flightCrewMembers", flightAssignments);
		dataset.put("pilot", this.getFirstAssignmentByDuty(flightAssignments, "PILOT"));
		dataset.put("copilot", this.getFirstAssignmentByDuty(flightAssignments, "COPILOT"));
		ArrayList<Object> cabinAttendants = new ArrayList<>();
		for (FlightAssignment fA : this.getAssignmentsByDuty(flightAssignments, "CABIN ATTENDANT"))
			cabinAttendants.add(this.getPossibleCrewsSelectChoices(fA, "CABIN ATTENDANT"));
		dataset.put("cabin_attendants", cabinAttendants);
		dataset.put("cabin_attendantExtra", this.getPossibleCrewsSelectChoices(flightAssignment, null));
		dataset.put("lead_attendant", this.getFirstAssignmentByDuty(flightAssignments, "LEAD ATTENDANT"));
		dataset.put("isSupLA", this.isSupLA(flightAssignment));
		dataset.put("Fid", this.getFid(flightAssignment));
		dataset.put("possibleCrewsForPilot", this.getPossibleCrewsSelectChoices(flightAssignment, "PILOT"));
		dataset.put("possibleCrewsForCopilot", this.getPossibleCrewsSelectChoices(flightAssignment, "COPILOT"));
		dataset.put("possibleLegs", this.getPossibleLegsSelectChoices(flightAssignment));
		dataset.put("statuses", this.getStatusesSelectChoices(flightAssignment));
		super.getResponse().addData(dataset);
	}

	private Object getDutySelectChoice(final FlightAssignment flightAssignment) {
		FlightAssignmentDuty current = Enum.valueOf(FlightAssignmentDuty.class, flightAssignment.getDuty().replace(" ", "_"));
		return SelectChoices.from(FlightAssignmentDuty.class, current);
	}

	private Integer getFid(final FlightAssignment flightAssignment) {
		Collection<FlightAssignment> flightAssignments = this.getFlightAssignments(flightAssignment);
		Integer supId = super.getRequest().getPrincipal().getActiveRealm().getId();
		Integer fid = flightAssignments.stream().filter(x -> x.getFlightCrewMember().getId() == supId).findFirst().get().getId();
		return fid;
	}

	private boolean isSupLA(final FlightAssignment flightAssignment) {
		// Si se muestra una asignación y no es "LEAD ATTENDANT", se considera de solo lectura
		Integer fid = this.getFid(flightAssignment);
		FlightAssignment f = this.repository.findById((int) fid);
		return f.getDuty().equals("LEAD ATTENDANT");
	}

	private Collection<FlightAssignment> getFlightAssignments(final FlightAssignment flightAssignment) {
		Integer flightAssignmentId = flightAssignment.getId();
		Integer legId = this.repository.findLegOfFlightAssignment(flightAssignmentId);
		return this.repository.findFlightAssignmentsOfLeg(legId);

	}

	private FlightAssignment getFirstAssignmentByDuty(final Collection<FlightAssignment> assignments, final String duty) {
		return assignments.stream().filter(a -> a.getDuty().equals(duty)).findFirst().orElse(null);
	}

	private Collection<FlightAssignment> getAssignmentsByDuty(final Collection<FlightAssignment> assignments, final String duty) {
		return assignments.stream().filter(a -> a.getDuty().equals(duty)).collect(Collectors.toList());
	}

	private Object getPossibleCrewsSelectChoices(final FlightAssignment flightAssignment, final String duty) {
		Date legDepartureTime = flightAssignment.getLeg().getScheduledDeparture();
		Date lowerBound = this.addHours(legDepartureTime, -12);
		Date upperBound = this.addHours(legDepartureTime, 12);
		Collection<FlightCrewMember> possibleCrews = this.repository.findCrewsMembersCandidateForLeg();

		Collection<FlightAssignment> flightAssignments = this.getFlightAssignments(flightAssignment);
		FlightAssignment flightAssigment2 = this.getFirstAssignmentByDuty(flightAssignments, duty);
		FlightCrewMember flightCrewMember;
		if (flightAssigment2 != null) {
			flightCrewMember = flightAssigment2.getFlightCrewMember();
			possibleCrews.add(flightCrewMember);

		} else
			flightCrewMember = null;

		return SelectChoices.from(possibleCrews, "id", flightCrewMember);
	}

	private Date addHours(final Date date, final int hours) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.HOUR, hours);
		return cal.getTime();
	}

	private Object getPossibleLegsSelectChoices(final FlightAssignment flightAssignment) {
		// Se utiliza ArgumentMatchers.anyCollection() como marcador; 
		// luego se añade el leg actual para que sea la única opción disponible.
		Collection<Leg> possibleLegs = this.repository.findAllFutureUnAssignLegs();
		if (possibleLegs.isEmpty())
			possibleLegs = ArgumentMatchers.anyCollection();
		possibleLegs.add(flightAssignment.getLeg());

		return SelectChoices.from(possibleLegs, "id", flightAssignment.getLeg());
	}

	private SelectChoices getStatusesSelectChoices(final FlightAssignment flightAssignment) {
		FlightAssignmentStatus current = Enum.valueOf(FlightAssignmentStatus.class, flightAssignment.getCurrentStatus());
		return SelectChoices.from(FlightAssignmentStatus.class, current);
	}
}
