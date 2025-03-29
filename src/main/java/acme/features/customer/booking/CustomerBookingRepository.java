
package acme.features.customer.booking;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.Booking;

@Repository
public interface CustomerBookingRepository extends AbstractRepository {

	@Query("SELECT DISTINCT b FROM Booking b")
	Collection<Booking> findAllBookings();

	@Query("SELECT DISTINCT b FROM Booking b WHERE b.customer.id = :tcustomerId")
	Collection<Booking> findAllByCustomer(int customerId);

	@Query("SELECT c.id FROM Customer c WHERE c.userAccount.id = :userId")
	int findCustomerIdByUserId(int userId);

}
