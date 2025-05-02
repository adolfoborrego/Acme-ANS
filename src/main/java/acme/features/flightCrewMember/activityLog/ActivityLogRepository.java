
package acme.features.flightCrewMember.activityLog;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.activityLog.ActivityLog;

@Repository
public interface ActivityLogRepository extends AbstractRepository {

	@Query("""
		    select al
		    from ActivityLog al
		    where al.flightAssignment.id = :flightAssignmentId
		""")
	Collection<ActivityLog> findActivityLogsOf(@Param("flightAssignmentId") int flightAssignmentId);

	@Query("select a from ActivityLog a where a.id = :id")
	ActivityLog findById(int id);

}
