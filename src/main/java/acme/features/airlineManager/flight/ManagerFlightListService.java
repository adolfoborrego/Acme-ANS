
package acme.features.airlineManager.flight;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flight.Flight;
import acme.realms.airlineManager.AirlineManager;

@GuiService
public class ManagerFlightListService extends AbstractGuiService<AirlineManager, Flight> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ManagerFlightRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status;

		status = super.getRequest().getPrincipal().hasRealmOfType(AirlineManager.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Collection<Flight> flights;
		Integer userAccountId;
		Integer managerId;

		userAccountId = super.getRequest().getPrincipal().getAccountId();

		managerId = this.repository.findManagerByUsserAccountId(userAccountId);

		flights = this.repository.findFlightsByManagerId(managerId);

		super.getBuffer().addData(flights);
	}

	@Override
	public void unbind(final Flight flight) {
		assert flight != null;
		boolean showCreate;
		Dataset dataset;
		dataset = super.unbindObject(flight, "tag", "indicator", "cost", "description");
		showCreate = super.getRequest().getPrincipal().hasRealm(flight.getAirlineManager());
		super.getResponse().addGlobal("showCreate", showCreate);
		super.getResponse().addData(dataset);
	}
}
