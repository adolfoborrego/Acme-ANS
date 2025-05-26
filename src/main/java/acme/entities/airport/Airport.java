
package acme.entities.airport;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidEmail;
import acme.client.components.validation.ValidString;
import acme.client.components.validation.ValidUrl;
import acme.constraints.ValidUniqueIataCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Airport extends AbstractEntity {

	// Serialisation version --------------------------------------------------

	private static final long		serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

	@Mandatory
	@ValidString(min = 1, max = 50)
	@Automapped
	private String					name;

	@Mandatory
	@ValidString(pattern = "^[A-Z]{3}$")
	@ValidUniqueIataCode
	private String					iataCode;

	@Mandatory
	@Valid
	@Automapped
	@Enumerated(EnumType.STRING)
	private AirportOperationalScope	operationalScope;

	@Mandatory
	@ValidString(min = 1, max = 50)
	@Automapped
	private String					city;

	@Mandatory
	@ValidString(min = 1, max = 50)
	@Automapped
	private String					country;

	@Optional
	@ValidUrl
	@Automapped
	private String					website;

	@Optional
	@ValidEmail
	@Automapped
	private String					emailAddress;

	@Optional
	@ValidString(pattern = "^\\+?\\d{6,15}$")
	@Automapped
	private String					contactPhoneNumber;

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------

}
