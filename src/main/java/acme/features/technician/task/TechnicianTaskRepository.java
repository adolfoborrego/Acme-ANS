
package acme.features.technician.task;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.Task;

@Repository
public interface TechnicianTaskRepository extends AbstractRepository {

	@Query("SELECT t.id FROM Technician t WHERE t.userAccount.id = :userId")
	int findTechnicianIdByUserId(int userId);

	@Query("SELECT t FROM Task t WHERE t.technician.id = :technicianId")
	Collection<Task> findTasksByTechnicianId(int technicianId);

	@Query("SELECT t FROM Task t WHERE t.maintenanceRecord.id = :maintenanceRecordId")
	Collection<Task> findTaskByMaintenanceRecordId(int maintenanceRecordId);

}
