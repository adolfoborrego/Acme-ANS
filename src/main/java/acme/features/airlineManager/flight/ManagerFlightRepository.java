
package acme.features.airlineManager.flight;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.flight.Flight;
import acme.entities.leg.Leg;

@Repository
public interface ManagerFlightRepository extends AbstractRepository {

	@Query("select f from Flight f")
	Collection<Flight> findAllFlights();

	@Query("select f from Flight f where f.id = :id")
	Flight findFlightById(int id);

	@Query("select f from Flight f where f.airlineManager.id = :managerId")
	Collection<Flight> findFlightsByManagerId(int managerId);

	@Query("select m.id from AirlineManager m where m.userAccount.id = :userAccountId")
	Integer findManagerByUsserAccountId(int userAccountId);

	@Query("SELECT l FROM Leg l WHERE l.flight.id = :flightId")
	Collection<Leg> findLegsByFlightId(int flightId);

}
