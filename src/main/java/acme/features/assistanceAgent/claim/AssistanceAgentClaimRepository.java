
package acme.features.assistanceAgent.claim;

import java.util.Collection;
import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.claim.Claim;
import acme.entities.leg.Leg;

@Repository
public interface AssistanceAgentClaimRepository extends AbstractRepository {

	@Query("SELECT ag.id FROM AssistanceAgent ag WHERE ag.userAccount.id = :userAccountId")
	Integer findAssistanceAgentIdByUserAccountId(int userAccountId);

	@Query("SELECT c.assistanceAgent.id FROM Claim c WHERE c.id = :claimId")
	Integer findAssistanceAgentIdByClaimId(int claimId);

	@Query("SELECT c FROM Claim c WHERE c.id = :claimId")
	Claim findClaimById(int claimId);

	@Query("SELECT l FROM Leg l WHERE l.id = :legId")
	Leg findLegById(int legId);

	@Query("SELECT l FROM Leg l WHERE l.published = true AND l.scheduledArrival < :now")
	Collection<Leg> findFinishedLegs(Date now);

	@Query("""
		    SELECT c
		    FROM Claim c
		    WHERE c.assistanceAgent.id = :assistanceAgentId
		      AND EXISTS (
		        SELECT tl
		        FROM TrackingLog tl
		        WHERE tl.claim.id = c.id
		          AND tl.published = true
		          AND tl.indicator <> 'PENDING'
		      )
		""")
	Collection<Claim> findCompletedClaimsByAssistanceAgentId(int assistanceAgentId);

	@Query("""
		    SELECT c
		    FROM Claim c
		    WHERE c.assistanceAgent.id = :assistanceAgentId
		      AND NOT EXISTS (
		        SELECT tl
		        FROM TrackingLog tl
		        WHERE tl.claim.id = c.id
		          AND tl.published = true
		          AND tl.indicator <> 'PENDING'
		      )
		""")
	Collection<Claim> findPendingClaimsByAssistanceAgentId(int assistanceAgentId);

}
