
package acme.features.airlineManager.leg;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.aircraft.Aircraft;
import acme.entities.airport.Airport;
import acme.entities.flight.Flight;
import acme.entities.leg.Leg;

@Repository
public interface ManagerLegRepository extends AbstractRepository {

	@Query("select l from Leg l")
	Collection<Leg> findAllLegs();

	@Query("select l from Leg l where l.id = :id")
	Leg findLegById(int id);

	@Query("select l from Leg l where l.flight.airlineManager.id = :managerId")
	Collection<Leg> findLegsByManagerId(int managerId);

	@Query("select m.id from AirlineManager m where m.userAccount.id = :userAccountId")
	Integer findManagerByUsserAccountId(int userAccountId);

	@Query("select l from Leg l where l.flight.airlineManager.id = :managerId order by l.scheduledDeparture asc")
	Collection<Leg> findLegsByManagerIdOrderedByScheduledDeparture(int managerId);

	@Query("select l from Leg l where l.flight.id = :flightId order by l.scheduledDeparture asc")
	Collection<Leg> findLegsByFlightIdOrderedByMoment(int flightId);

	@Query("SELECT f FROM Flight f WHERE f.id = :id")
	Flight findFlightById(int id);

	@Query("SELECT a FROM Airport a")
	Collection<Airport> findAllAirports();

	@Query("SELECT a FROM Aircraft a")
	Collection<Aircraft> findAllAircraft();

	@Query("SELECT a FROM Aircraft a WHERE a.id = :id")
	Aircraft findAircraftById(int id);

	@Query("SELECT l FROM Leg l WHERE l.flight.id = :flightId")
	List<Leg> findLegsByFlightId(int flightId);

}
