
package acme.features.assistanceAgent.claim;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.claim.Claim;

@Repository
public interface AssistanceAgentClaimRepository extends AbstractRepository {

	@Query("SELECT ag.id FROM AssistanceAgent ag WHERE ag.userAccount.id = :userAccountId")
	Integer findAssistanceAgentIdByUserAccountId(int userAccountId);

	@Query("SELECT c FROM Claim c WHERE c.assistanceAgent.id = :assistanceAgentId")
	Collection<Claim> findClaimsByAssistanceAgentId(int assistanceAgentId);

	@Query("SELECT c FROM Claim c WHERE c.id = :claimId")
	Claim findClaimById(int claimId);

	@Query("SELECT c.assistanceAgent.id FROM Claim c WHERE c.id = :claimId")
	Integer findAssistanceAgentIdByClaimId(int claimId);

	@Query("""
		    SELECT c
		    FROM Claim c
		    WHERE c.assistanceAgent.id = :assistanceAgentId
		      AND EXISTS (
		        SELECT tl
		        FROM TrackingLog tl
		        WHERE tl.claim.id = c.id
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
		          AND tl.indicator <> 'PENDING'
		      )
		""")
	Collection<Claim> findPendingClaimsByAssistanceAgentId(int assistanceAgentId);

}
