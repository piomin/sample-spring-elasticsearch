package pl.piomin.services.elasticsearch;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pl.piomin.services.elasticsearch.model.Department;
import pl.piomin.services.elasticsearch.model.Employee;
import pl.piomin.services.elasticsearch.model.Organization;
import pl.piomin.services.elasticsearch.repository.EmployeeRepository;

import java.util.List;
import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrganizationRepositoryTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationRepositoryTest.class);

    @Autowired
    EmployeeRepository repository;

    @Test
    public void testAdd() {
        Random r = new Random();
        Long id = repository.count();
        Employee employee = new Employee();
        employee.setId(id);
        employee.setName("John Smith" + r.nextInt(1000));
        employee.setAge(r.nextInt(100));
        employee.setPosition("Developer");
        employee.setDepartment(new Department(1L, "TestD"));
        employee.setOrganization(new Organization(1L, "TestO", "Test Street No. 1"));
        employee = repository.save(employee);
        Assert.assertNotNull(employee);
    }

    @Test
    public void testFindAll() {
        Iterable<Employee> employees = repository.findAll();
        employees.forEach(it -> LOGGER.info(it.toString()));
    }

    @Test
    public void testFindByOrganization() {
        List<Employee> employees = repository.findByOrganizationName("TestO");
        Assert.assertTrue(employees.size() > 0);
    }

}
