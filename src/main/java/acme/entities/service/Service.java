
package acme.entities.service;

import javax.persistence.Column;
import javax.persistence.Entity;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.client.components.validation.ValidUrl;
import acme.constraints.ValidPromotionCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Service extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidString(min = 1, max = 50)
	@Automapped
	private String				name;

	@Mandatory
	@ValidUrl
	@Automapped
	private String				linkToPicture;

	@Mandatory
	@ValidNumber(min = 0., max = 5.)
	@Automapped
	private Double				averageDwellTime;

	@Mandatory
	@Automapped
	private Boolean				promoted;

	@Optional
	@ValidString(pattern = "^[A-Z]{4}-[0-9]{2}$")
	@ValidPromotionCode
	@Column(unique = true)
	private String				promotionCode;

	@Optional
	@ValidMoney(min = 0, max = 1000)
	@Automapped
	private Money				discount;
}
