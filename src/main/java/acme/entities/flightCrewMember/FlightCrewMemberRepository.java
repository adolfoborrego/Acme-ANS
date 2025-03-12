
package acme.entities.flightCrewMember;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface FlightCrewMemberRepository extends AbstractRepository {

	@Query("SELECT count(a) > 1 FROM FlightCrewMember a WHERE a.employeeCode = :code")
	Boolean existsMemberWithCode(String code);

}
