package pl.piomin.services.elasticsearch;

import org.junit.*;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.piomin.services.elasticsearch.model.Department;
import pl.piomin.services.elasticsearch.model.Employee;
import pl.piomin.services.elasticsearch.model.Organization;
import pl.piomin.services.elasticsearch.repository.EmployeeRepository;

import java.util.List;

@SpringBootTest
@Testcontainers
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EmployeeRepositoryTest {

    @Autowired
    EmployeeRepository repository;

    @Container
    public static ElasticsearchContainer container = new ElasticsearchContainer();

    @DynamicPropertySource
    static void registerElasticsearchProperties(DynamicPropertyRegistry registry) {
        String uri = container.getContainerIpAddress() + ":" + container.getMappedPort(9300);
        registry.add("spring.data.elasticsearch.cluster-nodes", () -> uri);
    }

    @Test
    public void testAdd() {
        Employee employee = new Employee();
//        employee.setId(1L);
        employee.setName("John Smith");
        employee.setAge(33);
        employee.setPosition("Developer");
        employee.setDepartment(new Department(1L, "TestD"));
        employee.setOrganization(new Organization(1L, "TestO", "Test Street No. 1"));
        employee = repository.save(employee);
        Assert.assertNotNull(employee);
        Assert.assertNotNull(employee.getId());
    }

    @Test
    public void testFindAll() {
        Iterable<Employee> employees = repository.findAll();
        Assert.assertTrue(employees.iterator().hasNext());
    }

    @Test
    public void testFindByOrganization() {
        List<Employee> employees = repository.findByOrganizationName("TestO");
        Assert.assertTrue(employees.size() > 0);
    }

    @Test
    public void testFindByName() {
        List<Employee> employees = repository.findByName("John Smith");
        Assert.assertTrue(employees.size() > 0);
    }

}
