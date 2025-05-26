
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
	// Mejora las consultas que recuperan todos los tracking logs de un claim concreto
	@Index(name = "idx_trackinglog_claim", columnList = "claim_id"),
	// Optimiza las consultas que recuperan los tracking logs publicados de un claim
	@Index(name = "idx_trackinglog_claim_published", columnList = "claim_id, published"),
	// Acelera las subconsultas que filtran por claim, estado de publicación y estado del indicador
	@Index(name = "idx_trackinglog_claim_pub_ind", columnList = "claim_id, published, indicator")
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
