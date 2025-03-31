
package acme.features.customer.booking;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.booking.Booking;
import acme.entities.flight.Flight;

@Repository
public interface CustomerBookingRepository extends AbstractRepository {

	@Query("SELECT DISTINCT b FROM Booking b")
	Collection<Booking> findAllBookings();

	@Query("SELECT b FROM Booking b WHERE b.id = :bookingId")
	Booking findById(int bookingId);

	@Query("SELECT DISTINCT b FROM Booking b WHERE b.customer.id = :customerId")
	Collection<Booking> findAllByCustomer(int customerId);

	@Query("SELECT c.id FROM Customer c WHERE c.userAccount.id = :userId")
	int findCustomerIdByUserId(int userId);

	@Query("SELECT f FROM Flight f WHERE f.id = :id")
	Flight findFlightById(int id);

	@Query("SELECT DISTINCT f FROM Flight f")
	Collection<Flight> findAllFlights();

}
