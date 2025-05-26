
package acme.entities.claim;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidEmail;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidString;
import acme.client.helpers.SpringHelper;
import acme.entities.leg.Leg;
import acme.entities.trackingLog.TrackingLog;
import acme.entities.trackingLog.TrackingLogIndicator;
import acme.entities.trackingLog.TrackingLogRepository;
import acme.realms.assistanceAgent.AssistanceAgent;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "claim", indexes = {
	// Claims registradas por un AsssitanceAgent
	@Index(name = "idx_claim_assistance_agent", columnList = "assistance_agent_id")
})
public class Claim extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				registrationMoment;

	@Mandatory
	@ValidEmail
	@Automapped
	private String				passengerEmail;

	@Mandatory
	@ValidString(min = 1, max = 255)
	@Automapped
	private String				description;

	@Mandatory
	@Valid
	@Automapped
	@Enumerated(EnumType.STRING)
	private ClaimType			type;

	@Mandatory
	@Automapped
	private Boolean				published;

	// Derived attributes -----------------------------------------------------


	@Transient
	public TrackingLogIndicator getIndicator() {
		TrackingLogRepository repository = SpringHelper.getBean(TrackingLogRepository.class);
		List<TrackingLog> logs = repository.findPublishedTrackingLogsByClaimId(this.getId());
		return logs.isEmpty() ? TrackingLogIndicator.PENDING : logs.get(logs.size() - 1).getIndicator();
	}

	// Relationships ----------------------------------------------------------


	@Valid
	@Optional
	@ManyToOne
	private AssistanceAgent	assistanceAgent;

	@Valid
	@Optional
	@ManyToOne
	private Leg				leg;

}
