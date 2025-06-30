
package acme.entities.task;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.entities.maintenanceRecord.MaintenanceRecord;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(indexes = {
	@Index(name = "idx_maint_record", columnList = "maintenance_record_id"),
})
@Getter
@Setter
public class Task extends AbstractEntity {

	// Serialisation version -------------------------------------------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes ------------------------------------------------------------------------------------------------

	@Mandatory
	@Valid
	@Automapped
	@Enumerated(EnumType.STRING)
	private TaskType			type;

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
	private MaintenanceRecord	maintenanceRecord;
}
