
package acme.features.flightCrewMember.flightAssignment;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.flightAssignment.FlightAssignment;
import acme.entities.leg.Leg;
import acme.realms.flightCrewMember.FlightCrewMember;

@Repository
public interface FlightAssignmentRepository extends AbstractRepository {

	@Query("select j from FlightAssignment j where j.currentStatus != 'CANCELLED' and j.flightCrewMember.id = :memberId and j.leg.status = 'LANDED'")
	Collection<FlightAssignment> findAllMyCompletedFlightAssignments(Integer memberId);

	@Query("select j from FlightAssignment j where j.id = :flightAssignmentId")
	FlightAssignment findById(int flightAssignmentId);

	@Query("select j from FlightAssignment j where j.leg.id = :legId")
	Collection<FlightAssignment> findFlightAssignmentsOfLeg(Integer legId);

	@Query("select j.leg.id from FlightAssignment j where j.id = :flighAssignmentId ")
	int findLegOfFlightAssignment(int flighAssignmentId);

	@Query("select j from FlightAssignment j where  j.currentStatus != 'CANCELLED' and j.flightCrewMember.id = :memberId and (j.leg.status = 'DELAYED' or j.leg.status = 'ON_TIME')")
	Collection<FlightAssignment> findAllMyPlannedFlightAssignments(Integer memberId);

	@Query("select j from FlightCrewMember j where j.id = :memberId")
	FlightCrewMember findFlightCrewMemberById(Integer memberId);

	@Query("select j from Leg j where j.id = :legId")
	Leg findLegById(Integer legId);

	@Query("select l from Leg l " + "where (l.status = 'DELAYED' or l.status = 'ON_TIME') and l.published = false " + "and (select count(fa) from FlightAssignment fa where fa.leg = l) < 1")
	Collection<Leg> findAllFutureUnAssignLegs();

	@Query("select fcm from FlightCrewMember fcm " + "where fcm.availabilityStatus = 'AVAILABLE' and not exists (select fa from FlightAssignment fa where fa.flightCrewMember = fcm)")
	Collection<FlightCrewMember> findCrewsMembersCandidateForLeg();

}
