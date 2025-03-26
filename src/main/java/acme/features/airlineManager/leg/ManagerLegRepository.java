
package acme.features.airlineManager.leg;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
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

}
