
package acme.realms.flightCrewMember;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;

import acme.client.components.basis.AbstractRole;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.constraints.ValidCodeInitials;
import acme.constraints.ValidUniqueEmployeeCode;
import acme.entities.airline.Airline;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@ValidCodeInitials
@Table(name = "flight_crew_member", indexes = {

	@Index(name = "idx_fcm_airline_avail", columnList = "airline_id,availabilityStatus"), @Index(name = "idx_fcm_experience", columnList = "yearsOfExperience")
})
public class FlightCrewMember extends AbstractRole {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidString(pattern = "^[A-Z]{2,3}\\d{6}$")
	@ValidUniqueEmployeeCode
	@Column(unique = true)
	private String				employeeCode;

	@Mandatory
	@ValidString(pattern = "^\\+?\\d{6,15}$")
	@Automapped
	private String				phoneNumber;

	@Mandatory
	@ValidString(max = 255, min = 1)
	@Automapped
	String						languageSkills;

	@Mandatory
	@ValidString(pattern = "^(AVAILABLE|ON VACATION|ON LEAVE)$")
	@Automapped
	String						availabilityStatus;

	@Mandatory
	@ValidMoney(min = 0, max = 100000)
	@Automapped
	Money						salary;

	@Optional
	@ValidNumber(min = 0, max = 90)
	@Automapped
	Integer						yearsOfExperience;

	@Transient
	private String				identificator;


	/**
	 * Getter dinámico que genera una descripción enriquecida del tripulante.
	 */
	@Transient
	public String getIdentificator() {
		String code = this.employeeCode != null ? this.employeeCode : "N/A";
		String status = this.availabilityStatus != null ? this.availabilityStatus : "N/A";
		String airlineName = this.airline != null ? this.airline.getName() : "N/A";

		String fullName = "N/A";
		try {
			var identity = this.getIdentity(); // viene de AbstractRealm
			if (identity != null)
				fullName = identity.getFullName(); // suponiendo que DefaultUserIdentity tiene getFullName()
		} catch (Exception e) {
			// Puede pasar si identity es null o no es DefaultUserIdentity
			fullName = "N/A";
		}

		return String.format("Employee: %s | Name: %s | Status: %s | Airline: %s", code, fullName, status, airlineName);
	}

	/**
	 * Setter vacío para compatibilidad con frameworks que usan reflection.
	 */
	public void setIdentificator(final String identificator) {
		this.identificator = identificator;
	}


	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	Airline airline;

}
