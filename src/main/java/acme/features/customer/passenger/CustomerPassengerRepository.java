
package acme.features.customer.passenger;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.Passenger;

@Repository
public interface CustomerPassengerRepository extends AbstractRepository {

	@Query("SELECT DISTINCT p FROM Passenger p")
	Collection<Passenger> findAllPassengers();

	@Query("SELECT DISTINCT p FROM Passenger p WHERE p.customer.id = :customerId")
	Collection<Passenger> findAllByCustomer(int customerId);

	@Query("SELECT c.id FROM Customer c WHERE c.userAccount.id = :userId")
	int findCustomerIdByUserId(int userId);

}
