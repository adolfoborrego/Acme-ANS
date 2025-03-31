
package acme.features.technician.maintenanceRecord;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.aircraft.Aircraft;
import acme.entities.maintenanceRecord.MaintenanceRecord;
import acme.realms.technician.Technician;

@Repository
public interface TechnicianMaintRecordRepository extends AbstractRepository {

	@Query("SELECT DISTINCT mr FROM MaintenanceRecord mr")
	Collection<MaintenanceRecord> findAllMaintenanceRecord();

	@Query("SELECT t FROM Technician t WHERE t.userAccount.id = :userId")
	Technician findTechnicianByUserId(int userId);

	@Query("SELECT t.id FROM Technician t WHERE t.userAccount.id = :userId")
	int findTechnicianIdByUserId(int userId);

	@Query("SELECT mr FROM MaintenanceRecord mr WHERE mr.id = :maintenanceRecordId")
	MaintenanceRecord findById(int maintenanceRecordId);

	@Query("SELECT COUNT(t) FROM Task t WHERE t.maintenanceRecord.id = :maintenanceRecordId")
	int cuentaNumeroTasks(int maintenanceRecordId);

	@Query("SELECT a FROM Aircraft a WHERE a.registrationNumber = :registrationNumber")
	Aircraft findAircraftByRegistrationNumber(String registrationNumber);
}
