
package acme.entities.flightAssignment;

public enum FlightAssignmentDuty {

	PILOT, COPILOT, CABIN_ATTENDANT, LEAD_ATTENDANT;


	@Override
	public String toString() {
		return this.name().replace("_", " ");
	}

}
