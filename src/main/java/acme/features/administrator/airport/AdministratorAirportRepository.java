
package acme.features.administrator.airport;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.airport.Airport;

@Repository
public interface AdministratorAirportRepository extends AbstractRepository {

	@Query("select a from Airport a where a.iataCode = :iataCode")
	Airport findAirportByIataCode(@Param("iataCode") String iataCode);

	@Query("select count(a) > 0 from Airline a where a.iataCode = :iataCode")
	boolean existsAirlineByIataCode(@Param("iataCode") String iataCode);

	@Query("SELECT a FROM Airport a")
	Collection<Airport> findAllAirports();

	@Query("SELECT a FROM Airport a WHERE a.id = :id")
	Airport findAirportById(int id);

	@Query("select m.id from Administrator m where m.userAccount.id = :userAccountId")
	Integer findAdministratorByUsserAccountId(int userAccountId);
}
