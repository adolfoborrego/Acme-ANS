
package acme.realms.airlineManager;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface AirlineManagerRepository extends AbstractRepository {

	@Query("SELECT count(a) > 1 FROM AirlineManager a WHERE a.identifierNumber = :code")
	Boolean existsManagerWithCode(String code);

}
