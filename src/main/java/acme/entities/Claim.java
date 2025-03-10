
package acme.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidEmail;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidString;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Claim extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.DATE)
	private Date				registrationMoment;

	@Mandatory
	@ValidEmail
	private String				passengerEmail;

	@Mandatory
	@ValidString(max = 255)
	private String				description;

	@Mandatory
	@ValidString(pattern = "^(FLIGHT-ISSUES|LUGGAGE-ISSUES|SECURITY-INCIDENT|OTHER-ISSUES)$")
	private String				type;

	@Mandatory
	private Boolean				indicator;

	@Mandatory
	private Boolean				published;

	@Valid
	@Mandatory
	@ManyToOne
	private AssistanceAgent		assistanceAgent;

	@Valid
	@Mandatory
	@ManyToOne
	private Leg					leg;

}
