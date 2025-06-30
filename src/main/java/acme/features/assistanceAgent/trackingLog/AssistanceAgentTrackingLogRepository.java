
package acme.features.assistanceAgent.trackingLog;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.claim.Claim;
import acme.entities.trackingLog.TrackingLog;

@Repository
public interface AssistanceAgentTrackingLogRepository extends AbstractRepository {

	@Query("SELECT c FROM Claim c WHERE c.id = :claimId")
	Claim findClaimById(int claimId);

	@Query("SELECT ag.id FROM AssistanceAgent ag WHERE ag.userAccount.id = :userAccountId")
	int findAssistanceAgentIdByUserAccountId(int userAccountId);

	@Query("""
		    SELECT tl FROM TrackingLog tl
		    WHERE tl.claim.id = :claimId
		    ORDER BY
		        tl.published DESC,
		        CASE tl.indicator
		            WHEN 'PENDING' THEN 0
		            WHEN 'ACCEPTED' THEN 1
		            WHEN 'REJECTED' THEN 2
		            WHEN 'IN_REVIEW' THEN 3
		        END,
		        tl.resolutionPercentage ASC
		""")
	List<TrackingLog> findTrackingLogsByClaimId(int claimId);

	@Query("SELECT tl FROM TrackingLog tl WHERE tl.id = :trackingLogId")
	TrackingLog findTrackingLogById(int trackingLogId);

}
