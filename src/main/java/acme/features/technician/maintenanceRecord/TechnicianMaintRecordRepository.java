
package acme.features.technician.maintenanceRecord;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.MaintenanceRecord;

@Repository
public interface TechnicianMaintRecordRepository extends AbstractRepository {

	@Query("SELECT DISTINCT mr FROM MaintenanceRecord mr")
	Collection<MaintenanceRecord> findAllMaintenanceRecord();

	@Query("SELECT DISTINCT t.maintenanceRecord FROM Task t WHERE t.technician.id = :technicianId")
	Collection<MaintenanceRecord> findAllByTechnician(int technicianId);

	@Query("SELECT t.id FROM Technician t WHERE t.userAccount.id = :userId")
	int findTechnicianIdByUserId(int userId);
}
