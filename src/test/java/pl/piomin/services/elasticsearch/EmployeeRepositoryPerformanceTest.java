package pl.piomin.services.elasticsearch;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import net.jodah.concurrentunit.Waiter;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import pl.piomin.services.elasticsearch.model.Department;
import pl.piomin.services.elasticsearch.model.Employee;
import pl.piomin.services.elasticsearch.model.Organization;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Random;
import java.util.concurrent.TimeoutException;

public class EmployeeRepositoryPerformanceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeRepositoryPerformanceTest.class);

    @Rule
    public TestRule benchmarkRun = new BenchmarkRule();

    private final Random r = new Random();
    private final WebClient client = WebClient.builder()
            .baseUrl("http://localhost:8080")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
            .build();

    @Test
    @BenchmarkOptions(benchmarkRounds = 100, warmupRounds = 2)
    public void addTest() throws TimeoutException, InterruptedException {
        final Waiter waiter = new Waiter();
        Employee employee = new Employee();
        employee.setName("John Smith");
        employee.setAge(r.nextInt(100));
        employee.setPosition("Developer");
        employee.setDepartment(new Department((long) r.nextInt(10), "TestD"));
        employee.setOrganization(new Organization((long) r.nextInt(10), "TestO", "Test Street No. 1"));
        Mono<Employee> empMono = client.post().uri("/employees").body(Mono.just(employee), Employee.class).retrieve().bodyToMono(Employee.class);
        empMono.subscribe(employeeLocal -> {
            waiter.assertNotNull(employeeLocal);
            waiter.assertNotNull(employeeLocal.getId());
            waiter.resume();
        });
        waiter.await(5000);
    }

    @Test
    @BenchmarkOptions(concurrency = 10, benchmarkRounds = 1000, warmupRounds = 2)
    public void findByNameTest() throws TimeoutException, InterruptedException {
        final Waiter waiter = new Waiter();
        String name = "JohnSmith" + r.nextInt(1000000);
        Flux<Employee> employees = client.get().uri("/employees/{name}", name).retrieve().bodyToFlux(Employee.class);
        employees.count().subscribe(count -> {
            LOGGER.info("Found: {}", count);
            waiter.assertTrue(count > 0);
            waiter.resume();
        });
        waiter.await(5000);
    }

    @Test
    @BenchmarkOptions(concurrency = 10, benchmarkRounds = 100, warmupRounds = 2)
    public void findByOrganizationNameTest() throws TimeoutException, InterruptedException {
        final Waiter waiter = new Waiter();
        String organizationName = "TestO" + r.nextInt(5000);
        Flux<Employee> employees = client.get().uri("/employees/organization/{organizationName}", organizationName).retrieve().bodyToFlux(Employee.class);
        employees.count().subscribe(count -> {
            LOGGER.info("Found: {}", count);
            waiter.assertTrue(count > 0);
            waiter.resume();
        });
        waiter.await(5000);
    }

}
