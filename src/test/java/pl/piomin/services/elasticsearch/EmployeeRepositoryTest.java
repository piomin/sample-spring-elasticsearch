package pl.piomin.services.elasticsearch;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EmployeeRepositoryTest {

    @Autowired
    EmployeeRepository repository;

    @Container
//    @ServiceConnection
//    public static ElasticsearchContainer container = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:8.7.0");
    public static ElasticsearchContainer container = new ElasticsearchContainer();

    @DynamicPropertySource
    static void registerElasticsearchProperties(DynamicPropertyRegistry registry) {
        String uri = container.getContainerIpAddress() + ":" + container.getMappedPort(9300);
        registry.add("spring.data.elasticsearch.cluster-nodes", () -> uri);
    }

    @Test
    @Order(1)
    public void testAdd() {
        Employee employee = new Employee();
//        employee.setId(1L);
        employee.setName("John Smith");
        employee.setAge(33);
        employee.setPosition("Developer");
        employee.setDepartment(new Department(1L, "TestD"));
        employee.setOrganization(new Organization(1L, "TestO", "Test Street No. 1"));
        employee = repository.save(employee);
        assertNotNull(employee);
        assertNotNull(employee.getId());
    }

    @Test
    @Order(2)
    public void testFindAll() {
        Iterable<Employee> employees = repository.findAll();
        assertTrue(employees.iterator().hasNext());
    }

    @Test
    @Order(2)
    public void testFindByOrganization() {
        List<Employee> employees = repository.findByOrganizationName("TestO");
        assertTrue(employees.size() > 0);
    }

    @Test
    @Order(2)
    public void testFindByName() {
        List<Employee> employees = repository.findByName("John Smith");
        assertTrue(employees.size() > 0);
    }

}
