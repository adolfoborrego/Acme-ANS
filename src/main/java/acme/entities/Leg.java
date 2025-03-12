
package acme.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidString;
import acme.entities.airport.Airport;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Leg extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@Temporal(TemporalType.DATE)
	@Automapped
	private Date				scheduledDeparture;

	@Mandatory
	@Temporal(TemporalType.DATE)
	@Automapped
	private Date				scheduledArrival;

	@Mandatory
	@DecimalMin("0.0")
	@Automapped
	private Double				hours;

	@Mandatory
	@ValidString(pattern = "^(ON-TIME|DELAYED|CANCELLED|LANDED)$")
	@Automapped
	private String				status;

	@Mandatory
	@ManyToOne
	@Valid
	private Airport				departureAirport;

	@Optional
	@ManyToOne
	@Valid
	private Airport				arrivalAirport;

	@Optional
	@ManyToOne
	@Valid
	private Aircraft			aircraft;

	@Optional
	@ManyToOne
	@Valid
	private Flight				flight;

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------

}
