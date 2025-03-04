
package acme.entities;

import javax.persistence.Entity;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidString;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Flight extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidString(max = 50)
	private String				tag;

	@Mandatory
	private Boolean				indicator;

	@Mandatory
	@ValidMoney
	private Money				cost;

	@Optional
	@ValidString(max = 255)
	private String				description;

	@Mandatory
	private Boolean				published;

}
