
package acme.features.flightCrewMember.flightAssignment;

import java.util.Collection;
import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

	@Query("SELECT j FROM FlightAssignment j " + "WHERE j.currentStatus != 'CANCELLED' " + "AND j.flightCrewMember.id = :memberId " + "AND (j.leg.status = 'DELAYED' OR j.leg.status = 'ON_TIME') " + "AND j.leg.scheduledDeparture > :currentDate "
		+ "AND j.leg.scheduledArrival > :currentDate")
	Collection<FlightAssignment> findAllMyPlannedFlightAssignments(@Param("memberId") Integer memberId, @Param("currentDate") Date currentDate);

	@Query("select j from FlightCrewMember j where j.id = :memberId")
	FlightCrewMember findFlightCrewMemberById(Integer memberId);

	@Query("select j from Leg j where j.id = :legId")
	Leg findLegById(Integer legId);

	@Query("SELECT l FROM Leg l " + " WHERE l.status IN ('DELAYED','ON_TIME') " + "   AND l.published = true " + "   AND l.scheduledDeparture > :currentDate " + "   AND l.scheduledArrival   > :currentDate " + "   AND ( "
		+ "        (SELECT COUNT(fa)  FROM FlightAssignment fa  WHERE fa.leg = l) = 0 " + "     OR (SELECT COUNT(fa2) FROM FlightAssignment fa2 WHERE fa2.leg = l AND fa2.currentStatus <> 'CANCELLED') = 0 " + "       )")
	Collection<Leg> findAllFutureUnAssignedOrAllCancelledLegs(@Param("currentDate") Date currentDate);

	@Query("""
		    select fcm
		    from FlightCrewMember fcm
		    where fcm.availabilityStatus = 'AVAILABLE'
		      and fcm.airline.id = :airlineId
		      and not exists (
		          select fa
		          from FlightAssignment fa
		          where fa.flightCrewMember = fcm
		            and fa.currentStatus in ('CONFIRMED', 'PENDING')
		            and (
		                fa.leg.scheduledDeparture < :legEnd
		                and fa.leg.scheduledArrival > :legStart
		            )
		      )
		""")
	Collection<FlightCrewMember> findCrewsMembersCandidateForLegAvoidingOverlaps(@Param("airlineId") int airlineId, @Param("legStart") Date legStart, @Param("legEnd") Date legEnd);

}
