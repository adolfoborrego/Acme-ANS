
package acme.entities.claim;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.trackingLog.TrackingLog;

@Repository
public interface ClaimRepository extends AbstractRepository {

	@Query("SELECT tl FROM TrackingLog tl WHERE tl.claim.id = :claimId AND tl.published = true ORDER BY tl.lastUpdateMoment ASC")
	List<TrackingLog> findPublishedTrackingLogsByClaimId(int claimId);

}
