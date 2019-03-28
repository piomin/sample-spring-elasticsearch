package pl.piomin.services.elasticsearch.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.stereotype.Repository;
import pl.piomin.services.elasticsearch.model.Employee;

import java.util.List;

@Repository
public interface EmployeeRepository extends ElasticsearchCrudRepository<Employee, Long> {

    List<Employee> findByOrganizationName(String name);
    List<Employee> findByName(String name);

}
