
package acme.features.administrator.airline;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.airline.Airline;

@Repository
public interface AdministratorAirlineRepository extends AbstractRepository {

	@Query("SELECT a FROM Airline a")
	Collection<Airline> findAllAirlines();

	@Query("SELECT a FROM Airline a WHERE a.id = :id")
	Airline findAirlineById(int id);

	@Query("select m.id from Administrator m where m.userAccount.id = :userAccountId")
	Integer findAdministratorByUsserAccountId(int userAccountId);
}
