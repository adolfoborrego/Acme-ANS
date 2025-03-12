
package acme.entities.agent;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface AssistanceAgentRepository extends AbstractRepository {

	@Query("SELECT count(a) > 1 FROM AssistanceAgent a WHERE a.employeeCode = :code")
	Boolean existsAgentWithCode(String code);

}
