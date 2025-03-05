
package acme.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class MaintenanceRecord extends AbstractEntity {
	// Serialisation version -------------------------------------------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes ------------------------------------------------------------------------------------------------
	@Mandatory
	@ValidMoment
	@Temporal(TemporalType.TIMESTAMP)
	private Date				moment;

	@Mandatory
	@ValidString(pattern = "^(PENDING|IN PROGRESS|COMPLETED)$")
	private String				currentStatus;

	@Mandatory
	@ValidMoment
	@Temporal(TemporalType.TIMESTAMP)
	private Date				inspectionDueDate;

	@Mandatory
	@ValidNumber(min = 0)
	private Integer				estimatedCost;

	@Optional
	@ValidString(max = 255)
	private String				notes;

	@Mandatory
	private Boolean				published;

	@Mandatory
	@Valid
	@ManyToOne
	private Aircraft			aircraft;
}
