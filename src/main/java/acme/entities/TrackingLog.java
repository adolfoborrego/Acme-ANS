
package acme.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class TrackingLog extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.DATE)
	private Date				lastUpdateMoment;

	@Mandatory
	@ValidString(max = 50)
	private String				step;

	@Mandatory
	@ValidNumber
	@Min(0)
	@Max(100)
	private Integer				resolutionPercentage;

	@Mandatory
	private Boolean				indicator;

	@Mandatory
	@ValidString(max = 255)
	private String				resolution;

	@Mandatory
	private Boolean				published;

	@Valid
	@Mandatory
	@ManyToOne
	private Claim				claim;

}
