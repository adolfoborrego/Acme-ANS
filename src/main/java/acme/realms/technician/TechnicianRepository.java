
package acme.realms.technician;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.maintenanceRecord.MaintenanceRecord;

@Repository
public interface TechnicianRepository extends AbstractRepository {

	@Query("SELECT mr FROM MaintenanceRecord mr WHERE mr.technician.id = :technicianId")
	Collection<MaintenanceRecord> findAllMaintenanceRecordById(int technicianId);

}
