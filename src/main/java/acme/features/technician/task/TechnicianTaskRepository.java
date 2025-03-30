
package acme.features.technician.task;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.Task;
import acme.entities.maintenanceRecord.MaintenanceRecord;

@Repository
public interface TechnicianTaskRepository extends AbstractRepository {

	@Query("SELECT t.id FROM Technician t WHERE t.userAccount.id = :userId")
	int findTechnicianIdByUserId(int userId);

	@Query("SELECT t FROM Task t WHERE t.id = :id")
	Task findById(int id);

	@Query("SELECT t FROM Task t WHERE t.maintenanceRecord.id = :maintenanceRecordId")
	Collection<Task> findTaskByMaintenanceRecordId(int maintenanceRecordId);

	@Query("SELECT mr FROM MaintenanceRecord mr WHERE mr.id = :id")
	MaintenanceRecord findMaintenanceRecordById(int id);
}
