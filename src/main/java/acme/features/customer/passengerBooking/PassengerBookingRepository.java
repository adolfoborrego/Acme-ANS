
package acme.features.customer.passengerBooking;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.Passenger;
import acme.entities.PassengerBooking;
import acme.entities.booking.Booking;

@Repository
public interface PassengerBookingRepository extends AbstractRepository {

	@Query("SELECT DISTINCT pb.passenger FROM PassengerBooking pb WHERE pb.booking.id = :bookingId")
	Collection<Passenger> findPassengersByBookingId(int bookingId);

	@Query("SELECT count(pb) FROM PassengerBooking pb WHERE pb.booking.id = :bookingId")
	Integer countNumberOfPassengersOfBooking(int bookingId);

	@Query("SELECT DISTINCT p FROM Passenger p")
	Collection<Passenger> findAllPassengers();

	@Query("SELECT DISTINCT b FROM Booking b WHERE b.id = :bookingId")
	Booking findBookingById(int bookingId);

	@Query("SELECT c.id FROM Customer c WHERE c.userAccount.id = :userId")
	int findCustomerIdByUserId(int userId);

	@Query("SELECT DISTINCT pb FROM PassengerBooking pb")
	Collection<PassengerBooking> findAllPassengerBooking();

	@Query("SELECT DISTINCT pb FROM PassengerBooking pb WHERE pb.id = :passengerBookingId")
	PassengerBooking findPassengersBookingById(int passengerBookingId);

	@Query("SELECT DISTINCT p FROM Passenger p WHERE p.customer.id = :id")
	Collection<Passenger> findPassengersByCustomerId(int id);
}
