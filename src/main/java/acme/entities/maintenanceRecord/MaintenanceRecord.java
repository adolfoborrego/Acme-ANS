
package acme.entities.maintenanceRecord;

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
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidString;
import acme.entities.aircraft.Aircraft;
import acme.realms.technician.Technician;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(indexes = {
	@Index(name = "idx_technician", columnList = "technician_id"), @Index(name = "idx_aircraft", columnList = "aircraft_id"),
})
@Getter
@Setter
public class MaintenanceRecord extends AbstractEntity {
	// Serialisation version -------------------------------------------------------------------------------------

	private static final long		serialVersionUID	= 1L;

	// Attributes ------------------------------------------------------------------------------------------------
	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date					moment;

	@Mandatory
	@Automapped
	@Valid
	@Enumerated(EnumType.STRING)
	private MaintenanceRecordStatus	currentStatus;

	@Mandatory
	@ValidMoment
	@Temporal(TemporalType.TIMESTAMP)
	private Date					inspectionDueDate;

	@Mandatory
	@Automapped
	@ValidMoney(min = 0., max = 999999999)
	private Money					estimatedCost;

	@Optional
	@Automapped
	@ValidString(min = 0, max = 255)
	private String					notes;

	@Mandatory
	@Automapped
	private Boolean					published;

	@Optional
	@Automapped
	@Valid
	@ManyToOne(optional = false)
	private Aircraft				aircraft;

	@Optional
	@Automapped
	@Valid
	@ManyToOne(optional = false)
	private Technician				technician;


	public static boolean isPrefixValid(final MaintenanceRecord mr) {
		List<String> validMoneyPrefixes = List.of("USD", "EUR", "GBP", "JPY", "CAD", "AUD", "CHF", "CNY", "MXN", "BRL", "INR", "KRW", "SEK", "NOK", "DKK", "RUB", "ARS", "CLP", "COP", "ZAR");
		return validMoneyPrefixes.contains(mr.getEstimatedCost().getCurrency());
	}
}
