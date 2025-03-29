
package acme.entities.maintenanceRecord;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.realms.Technician;

@Repository
public interface MaintenanceRecordRepository extends AbstractRepository {

	@Query("SELECT t.technician FROM Task t WHERE t.maintenanceRecord.id = :maintenanceRecordId")
	Technician findTechnicianByMaintenanceRecordId(int maintenanceRecordId);
}
