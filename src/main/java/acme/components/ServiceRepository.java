
package acme.components;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.helpers.RandomHelper;
import acme.client.repositories.AbstractRepository;
import acme.entities.service.Service;

@Repository
public interface ServiceRepository extends AbstractRepository {

	@Query("SELECT COUNT(s) FROM Service s WHERE s.promoted = true")
	int countPromotedServices();

	@Query("SELECT s FROM Service s WHERE s.promoted = true")
	List<Service> findAllPromotedServices(PageRequest pageable);

	default Service findRandomPromotedServices() {
		Service result;
		int count, index;
		PageRequest page;
		List<Service> list;

		count = this.countPromotedServices();
		if (count == 0)
			result = null;
		else {
			index = RandomHelper.nextInt(0, count);

			page = PageRequest.of(index, 1, Sort.by(Direction.ASC, "id"));
			list = this.findAllPromotedServices(page);
			result = list.isEmpty() ? null : list.get(0);
		}

		return result;
	}

}
