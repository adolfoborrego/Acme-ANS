
package acme.features.customer.passenger;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.Passenger;
import acme.entities.booking.Booking;
import acme.realms.Customer;

@Repository
public interface CustomerPassengerRepository extends AbstractRepository {

	@Query("SELECT DISTINCT p FROM Passenger p")
	Collection<Passenger> findAllPassengers();

	@Query("SELECT DISTINCT p FROM Passenger p WHERE p.customer.id = :customerId")
	Collection<Passenger> findAllByCustomer(int customerId);

	@Query("SELECT c.id FROM Customer c WHERE c.userAccount.id = :userId")
	int findCustomerIdByUserId(int userId);

	@Query("SELECT p FROM Passenger p WHERE p.id = :passengerId")
	Passenger findById(int passengerId);

	@Query("SELECT b FROM Booking b WHERE b.id = :bookingId")
	Booking findBookingById(int bookingId);

	@Query("SELECT c FROM Customer c where c.id = :customerId")
	Customer findCustomerById(int customerId);

}
