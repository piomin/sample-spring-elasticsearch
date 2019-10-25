package pl.piomin.services.elasticsearch;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import pl.piomin.services.elasticsearch.model.Department;
import pl.piomin.services.elasticsearch.model.Employee;
import pl.piomin.services.elasticsearch.model.Organization;
import pl.piomin.services.elasticsearch.repository.EmployeeRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EmployeeRepositoryTest {

    @ClassRule
    public static ElasticsearchContainer container = new ElasticsearchContainer();
    @Autowired
    EmployeeRepository repository;

    @BeforeClass
    public static void before() {
        System.setProperty("spring.data.elasticsearch.client.reactive.endpoints", container.getContainerIpAddress() + ":" + container.getMappedPort(9200));
    }

    @Test
    public void testAdd() {
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setName("John Smith");
        employee.setAge(33);
        employee.setPosition("Developer");
        employee.setDepartment(new Department(1L, "TestD"));
        employee.setOrganization(new Organization(1L, "TestO", "Test Street No. 1"));
        Mono<Employee> employeeSaved = repository.save(employee);
        Assert.assertNotNull(employeeSaved.block());
    }

    @Test
    public void testFindAll() {
        Flux<Employee> employees = repository.findAll();
        Assert.assertTrue(employees.count().block() > 0);
    }

    @Test
    public void testFindByOrganization() {
        Flux<Employee> employees = repository.findByOrganizationName("TestO");
        Assert.assertTrue(employees.count().block() > 0);
    }

    @Test
    public void testFindByName() {
        Flux<Employee> employees = repository.findByName("John Smith");
        Assert.assertTrue(employees.count().block() > 0);
    }

}
