
package acme.features.administrator.aircraft;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.aircraft.Aircraft;
import acme.entities.airline.Airline;

@Repository
public interface AdministratorAircraftRepository extends AbstractRepository {

	@Query("SELECT a FROM Aircraft a")
	Collection<Aircraft> findAllAircrafts();

	@Query("SELECT a FROM Aircraft a WHERE a.id = :aircraftId")
	Aircraft findById(int aircraftId);

	@Query("SELECT a FROM Airline a")
	Collection<Airline> findAllAirlines();
}
