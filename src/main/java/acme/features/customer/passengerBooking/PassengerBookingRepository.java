
package acme.features.customer.passengerBooking;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.Passenger;

@Repository
public interface PassengerBookingRepository extends AbstractRepository {

	@Query("SELECT DISTINCT pb.passenger FROM PassengerBooking pb WHERE pb.booking.id = :bookingId")
	Collection<Passenger> findPassengersByBookingId(int bookingId);

	@Query("SELECT count(pb) FROM PassengerBooking pb WHERE pb.booking.id = :bookingId")
	Integer countNumberOfPassengersOfBooking(int bookingId);
}
