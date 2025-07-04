
package acme.realms.technician;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;

import acme.client.components.basis.AbstractRole;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.client.helpers.SpringHelper;
import acme.constraints.ValidCodeInitials;
import acme.entities.maintenanceRecord.MaintenanceRecord;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@ValidCodeInitials
//It is not necessary to create indexes because by not declaring any manually, 
//the indexes of the relationships are created automatically.
public class Technician extends AbstractRole {

	// Serialisation version -------------------------------------------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes ------------------------------------------------------------------------------------------------
	@Mandatory
	@ValidString(pattern = "^[A-Z]{2,3}\\d{6}$")
	@Column(unique = true)
	private String				licenseNumber;

	@Mandatory
	@ValidString(pattern = "^\\+?\\d{6,15}$")
	@Automapped
	private String				phoneNumber;

	@Mandatory
	@ValidString(min = 1, max = 50)
	@Automapped
	private String				specialisation;

	@Mandatory
	@Automapped
	private Boolean				annualHealthTest;

	@Mandatory
	@ValidNumber(min = 0, max = 80)
	@Automapped
	private Integer				yearsOfExperience;

	@Optional
	@ValidString(min = 0, max = 255)
	@Automapped
	private String				certifications;


	public Collection<MaintenanceRecord> getMaintenanceRecords() {
		TechnicianRepository repository;
		Collection<MaintenanceRecord> res;

		repository = SpringHelper.getBean(TechnicianRepository.class);
		res = repository.findAllMaintenanceRecordById(this.getId());

		return res;
	}

}
