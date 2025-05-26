
package acme.entities.flightAssignment;

import java.util.Date;

import javax.persistence.Entity;
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
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidString;
import acme.entities.leg.Leg;
import acme.realms.flightCrewMember.FlightCrewMember;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "flight_assignment", indexes = {
	// 1) Búsqueda por tripulante + estado (usado en findAllMyCompleted... y findAllMyPlanned...)

	@Index(name = "idx_fa_member_status", columnList = "currentStatus, flight_crew_member_id"),
	// 2) Recuperar asignaciones de una ruta (findFlightAssignmentsOfLeg)

	@Index(name = "idx_fa_leg", columnList = "leg_id"),
	// 3) Para futuros filtros/ordenamientos por fecha de última actualización

	@Index(name = "idx_fa_last_update", columnList = "momentOfLastUpdate")
})

public class FlightAssignment extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidString(pattern = "^(PILOT|COPILOT|LEAD ATTENDANT|CABIN ATTENDANT)$")
	@Automapped
	String						duty;

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	Date						momentOfLastUpdate;

	@Mandatory
	@ValidString(pattern = "^(CONFIRMED|PENDING|CANCELLED)$")
	@Automapped
	String						currentStatus;

	@Optional
	@ValidString(max = 255, min = 1)
	@Automapped
	String						remarks;

	// Transient identificator for bean property reflection
	@Transient
	private String				identificator;


	@Transient
	public String getIdentificator() {
		String dutyCode = this.duty != null ? this.duty : "N/A";
		String legInfo = this.leg != null && this.leg.getIdentificator() != null ? this.leg.getIdentificator() : "N/A";

		return String.format("[Duty: %s]  [Leg: %s]", dutyCode, legInfo);
	}

	public void setIdentificator(final String identificator) {
		this.identificator = identificator;
	}


	@Mandatory
	@Valid
	@Automapped
	@ManyToOne(optional = false)
	FlightCrewMember	flightCrewMember;

	@Mandatory
	@Valid
	@Automapped
	@ManyToOne /* ( optional = false ) */
	Leg					leg;
}
