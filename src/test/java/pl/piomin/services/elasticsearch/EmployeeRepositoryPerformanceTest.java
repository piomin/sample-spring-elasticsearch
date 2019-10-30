package pl.piomin.services.elasticsearch;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.web.client.TestRestTemplate;
import pl.piomin.services.elasticsearch.model.Department;
import pl.piomin.services.elasticsearch.model.Employee;
import pl.piomin.services.elasticsearch.model.Organization;

import java.util.Random;

public class EmployeeRepositoryPerformanceTest extends AbstractBenchmark {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeRepositoryPerformanceTest.class);

    @Rule
    public TestRule benchmarkRun = new BenchmarkRule();

    private TestRestTemplate template = new TestRestTemplate();
    private Random r = new Random();

    @Test
    @BenchmarkOptions(concurrency = 30, benchmarkRounds = 500, warmupRounds = 2)
    public void addTest() {
        Employee employee = new Employee();
        employee.setName("John Smith");
        employee.setAge(r.nextInt(100));
        employee.setPosition("Developer");
        employee.setDepartment(new Department((long) r.nextInt(1000), "TestD"));
        employee.setOrganization(new Organization((long) r.nextInt(100), "TestO", "Test Street No. 1"));
        employee = template.postForObject("http://localhost:8080/employees", employee, Employee.class);
        Assert.assertNotNull(employee);
        Assert.assertNotNull(employee.getId());
    }

    @Test
    @BenchmarkOptions(concurrency = 30, benchmarkRounds = 500, warmupRounds = 2)
    public void findByNameTest() {
        String name = "JohnSmith" + r.nextInt(1000000);
        Employee[] employees = template.getForObject("http://localhost:8080/employees/{name}", Employee[].class, name);
        LOGGER.info("Found: {}", employees.length);
        Assert.assertNotNull(employees);
    }

    @Test
    @BenchmarkOptions(concurrency = 30, benchmarkRounds = 500, warmupRounds = 2)
    public void findByOrganizationNameTest() {
        String organizationName = "TestO" + r.nextInt(5000);
        Employee[] employees = template.getForObject("http://localhost:8080/employees/organization/{organizationName}", Employee[].class, organizationName);
        LOGGER.info("Found: {}", employees.length);
        Assert.assertNotNull(employees);
    }

}
