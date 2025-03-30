
package acme.entities.claim;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
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
		TrackingLogRepository trackingLogRepository;
		List<TrackingLog> trackingLogs;
		TrackingLogIndicator indicator;
		//		Integer numberOfTrackingLogs;
		trackingLogRepository = SpringHelper.getBean(TrackingLogRepository.class);
		trackingLogs = trackingLogRepository.findPublishedTrackingLogsByClaimId(this.getId());
		//		Esta sería la implementación correcta en caso de conseguir que funcione el validador de TrackingLog
		//		numberOfTrackingLogs = trackingLogs.size();
		//		indicator = numberOfTrackingLogs == 0 ? TrackingLogIndicator.PENDING : trackingLogs.get(numberOfTrackingLogs - 1).getIndicator();
		if (trackingLogs.isEmpty() || trackingLogs.stream().allMatch(log -> log.getIndicator() == TrackingLogIndicator.PENDING))
			indicator = TrackingLogIndicator.PENDING;
		else if (trackingLogs.stream().anyMatch(log -> log.getIndicator() == TrackingLogIndicator.IN_REVIEW))
			indicator = TrackingLogIndicator.IN_REVIEW;
		else if (trackingLogs.stream().anyMatch(log -> log.getIndicator() == TrackingLogIndicator.ACCEPTED))
			indicator = TrackingLogIndicator.ACCEPTED;
		else if (trackingLogs.stream().anyMatch(log -> log.getIndicator() == TrackingLogIndicator.REJECTED))
			indicator = TrackingLogIndicator.REJECTED;
		else
			indicator = TrackingLogIndicator.PENDING;
		return indicator;
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
