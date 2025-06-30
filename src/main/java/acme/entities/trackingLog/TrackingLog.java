
package acme.entities.trackingLog;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidScore;
import acme.client.components.validation.ValidString;
import acme.entities.claim.Claim;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "tracking_log", indexes = {
	// This index is necessary because having declared other indexes manually, 
	// the indexes of the relationships are not generated automatically. 
	@Index(name = "idx_tracking_log_claim", columnList = "claim_id"),
	// Claim and published
	@Index(name = "idx_tracking_log_claim_published", columnList = "claim_id, published"),
	// Claim, published and indicator
	@Index(name = "idx_tracking_log_claim_published_indicator", columnList = "claim_id, published, indicator")
})
public class TrackingLog extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long		serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date					lastUpdateMoment;

	@Mandatory
	@ValidString(min = 1, max = 50)
	@Automapped
	private String					step;

	@Mandatory
	@ValidScore
	@Automapped
	private Double					resolutionPercentage;

	@Mandatory
	@Valid
	@Automapped
	@Enumerated(EnumType.STRING)
	private TrackingLogIndicator	indicator;

	@Optional
	@ValidString(min = 1, max = 255)
	@Automapped
	private String					resolution;

	@Mandatory
	@Automapped
	private Boolean					published;

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------

	@Valid
	@Optional
	@ManyToOne
	private Claim					claim;

}
