
package acme.features.flightCrewMember.flightAssignment;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.flightAssignment.FlightAssignment;
import acme.realms.flightCrewMember.FlightCrewMember;

@Repository
public interface FlightAssignmentRepository extends AbstractRepository {

	@Query("select j from FlightAssignment j where j.flightCrewMember.id = :memberId and j.leg.status = 'LANDED'")
	Collection<FlightAssignment> findAllMyCompletedFlightAssignments(Integer memberId);

	@Query("select j from FlightAssignment j where j.id = :flightAssignmentId")
	FlightAssignment findById(int flightAssignmentId);

	@Query("select j.flightCrewMember from FlightAssignment j where j.leg.id = :legId")
	Collection<FlightCrewMember> findFlightCrewMembersOfLeg(Integer legId);

	@Query("select j.leg.id from FlightAssignment j where j.id = :flighAssignmentId ")
	int findLegOfFlightAssignment(int flighAssignmentId);

	@Query("select j from FlightAssignment j where j.flightCrewMember.id = :memberId and (j.leg.status = 'DELAYED' or j.leg.status = 'ON-TIME')")
	Collection<FlightAssignment> findAllMyPlannedFlightAssignments(Integer memberId);

}
