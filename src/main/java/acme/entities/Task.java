
package acme.entities;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Task extends AbstractEntity {

	// Serialisation version -------------------------------------------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes ------------------------------------------------------------------------------------------------

	@Mandatory
	@ValidString(pattern = "^(MAINTENANCE|INSPECTION|REPAIR|SYSTEM CHECK)$")
	@Automapped
	private String				type;

	@Mandatory
	@ValidString(min = 1, max = 255)
	@Automapped
	private String				description;

	@Mandatory
	@ValidNumber(min = 0, max = 10)
	@Automapped
	private Integer				priority;

	@Mandatory
	@ValidNumber(min = 0, max = 2000)
	@Automapped
	private Integer				estimatedDuration;

	@Mandatory
	@Automapped
	private Boolean				published;

	@Optional
	@Valid
	@Automapped
	@ManyToOne(optional = false)
	private Technician			technician;

	@Optional
	@Valid
	@Automapped
	@ManyToOne(optional = false)
	private MaintenanceRecord	maintenanceRecord;
}
