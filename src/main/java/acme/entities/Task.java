
package acme.entities;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Task extends AbstractEntity {

	@Mandatory
	@ValidString(pattern = "^(MAINTENANCE|INSPECTION|REPAIR|SYSTEM CHECK)$")
	private String				type;
	@Mandatory
	@ValidString(max = 255)
	private String				description;

	@Mandatory
	@ValidNumber(min = 0, max = 10)
	private Integer				priority;

	@Mandatory
	@ValidNumber
	private Integer				estimatedDuration;

	@Mandatory
	private Boolean				published;

	@Mandatory
	@Valid
	@ManyToOne
	private Technician			technician;

	@Mandatory
	@Valid
	@ManyToOne
	private MaintenanceRecord	maintenanceRecord;
}
