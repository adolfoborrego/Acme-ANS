
package acme.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.DecimalMin;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidString;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Leg extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@Temporal(TemporalType.DATE)
	private Date				scheduledDeparture;

	@Mandatory
	@Temporal(TemporalType.DATE)
	private Date				scheduledArrival;

	@Mandatory
	@DecimalMin("0.0")
	private Double				hours;

	@Mandatory
	@ValidString(pattern = "^(ON-TIME|DELAYED|CANCELLED|LANDED)$")
	private String				status;

	@Mandatory
	@ManyToOne
	private Airport				departureAirport;

	@Mandatory
	@ManyToOne
	private Airport				arrivalAirport;

	@Mandatory
	@ManyToOne
	private Aircraft			aircraft;

}
