
package acme.entities.trackingLog;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;

@Repository
public interface TrackingLogRepository extends AbstractRepository {

	@Query("SELECT tl FROM TrackingLog tl WHERE tl.claim.id = :claimId AND tl.published = true ORDER BY tl.resolutionPercentage ASC")
	List<TrackingLog> findPublishedTrackingLogsByClaimId(int claimId);

}
