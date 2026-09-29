package pl.piomin.services.elasticsearch;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.data.elasticsearch.core.RefreshPolicy;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.piomin.services.elasticsearch.model.Department;
import pl.piomin.services.elasticsearch.model.Employee;
import pl.piomin.services.elasticsearch.model.Organization;
import pl.piomin.services.elasticsearch.repository.EmployeeRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EmployeeRepositoryTest {

    @Autowired
    EmployeeRepository repository;

    @Autowired
    ReactiveElasticsearchOperations operations;

    @Container
    public static ElasticsearchContainer container =
            new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:8.17.0")
                    .withEnv("xpack.security.enabled", "false");

    @DynamicPropertySource
    static void registerElasticsearchProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.elasticsearch.uris", container::getHttpHostAddress);
    }

    @Test
    @Order(1)
    public void testAdd() {
        Employee employee = new Employee();
        employee.setId("1");
        employee.setName("John Smith");
        employee.setAge(33);
        employee.setPosition("Developer");
        employee.setDepartment(new Department(1L, "TestD"));
        employee.setOrganization(new Organization(1L, "TestO", "Test Street No. 1"));
        operations.withRefreshPolicy(RefreshPolicy.IMMEDIATE);
        Mono<Employee> employeeSaved = repository.save(employee);
        Employee saved = employeeSaved.block();
        assertNotNull(saved);

        // Force index refresh to make the document searchable
        operations.indexOps(Employee.class).refresh().block();
    }

    @Test
    @Order(2)
    public void testFindAll() {
        Flux<Employee> employees = repository.findAll();
        assertTrue(employees.count().block() > 0);
    }

    @Test
    @Order(3)
    public void testFindByOrganization() {
        Flux<Employee> employees = repository.findByOrganizationName("TestO");
        assertTrue(employees.count().block() > 0);
    }

    @Test
    @Order(4)
    public void testFindByName() {
        Flux<Employee> employees = repository.findByName("John Smith");
        assertTrue(employees.count().block() > 0);
    }

}
