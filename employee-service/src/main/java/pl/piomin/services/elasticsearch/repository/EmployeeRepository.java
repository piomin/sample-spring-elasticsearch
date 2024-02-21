package pl.piomin.services.elasticsearch.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pl.piomin.services.elasticsearch.model.Employee;
import reactor.core.publisher.Flux;

@Repository
public interface EmployeeRepository extends ReactiveCrudRepository<Employee, Long> {

    Flux<Employee> findByOrganizationName(String name);
    Flux<Employee> findByName(String name);

}
