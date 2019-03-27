package pl.piomin.services.elasticsearch.repository;

import org.springframework.data.repository.CrudRepository;
import pl.piomin.services.elasticsearch.model.Employee;

import java.util.List;

public interface EmployeeRepository extends CrudRepository<Employee, Long> {

    List<Employee> findByOrganizationName(String name);
    List<Employee> findByName(String name);
}
