
package acme.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidMoney;
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
	@Automapped
	@Temporal(TemporalType.TIMESTAMP)
	private Date				moment;

	@Mandatory
	@Automapped
	@ValidString(pattern = "^(PENDING|IN PROGRESS|COMPLETED)$")
	private String				currentStatus;

	@Mandatory
	@ValidMoment
	@Automapped
	@Temporal(TemporalType.TIMESTAMP)
	private Date				inspectionDueDate;

	@Mandatory
	@Automapped
	@ValidMoney(min = 0.)
	private Money				estimatedCost;

	@Optional
	@Automapped
	@ValidString(max = 255)
	private String				notes;

	@Mandatory
	@Automapped
	private Boolean				published;

	@Optional
	@Automapped
	@Valid
	@ManyToOne
	private Aircraft			aircraft;
}
