
package acme.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import acme.entities.service.Service;

@ControllerAdvice
public class ServicePromoted {

	// Internal state ---------------------------------------------------------

	@Autowired
	private ServiceRepository repository;

	// Beans ------------------------------------------------------------------


	@ModelAttribute("service")
	public Service getService() {
		Service result;

		try {
			result = this.repository.findRandomPromotedServices();
		} catch (final Throwable oops) {
			result = null;
		}

		return result;
	}

}
