package pl.piomin.services.elasticsearch.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.piomin.services.elasticsearch.model.Department;
import pl.piomin.services.elasticsearch.model.Employee;
import pl.piomin.services.elasticsearch.model.Organization;
import pl.piomin.services.elasticsearch.repository.EmployeeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

	private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeController.class);

	@Autowired
	EmployeeRepository repository;

	@PostMapping
	public Mono<Employee> add(@RequestBody Employee employee) {
		return repository.save(employee);
	}

	@GetMapping("/{name}")
	public Flux<Employee> findByName(@PathVariable("name") String name) {
		return repository.findByName(name);
	}

	@GetMapping
	public Flux<Employee> findAll() {
		return repository.findAll();
	}

	@GetMapping("/count/all")
	public Mono<Long> count() {
		return repository.count();
	}

	@GetMapping("/organization/{organizationName}")
	public Flux<Employee> findByOrganizationName(@PathVariable("organizationName") String organizationName) {
		return repository.findByOrganizationName(organizationName);
	}

	@PostMapping("/generate")
	public Flux<Employee> generateMulti() {
		return repository.saveAll(employees()).doOnNext(employee -> LOGGER.info("Added: {}", employee));
	}

	private List<Employee> employees() {
		List<Employee> employees = new ArrayList<>();
		for (int i = 0; i < 200; i++) {
			Random r = new Random();
			Employee employee = new Employee();
			employee.setName("JohnSmith" + r.nextInt(1000000));
			employee.setAge(r.nextInt(100));
			employee.setPosition("Developer");
			int departmentId = r.nextInt(5000);
			employee.setDepartment(new Department((long) departmentId, "TestD" + departmentId));
			int organizationId = departmentId % 100;
			employee.setOrganization(new Organization((long) organizationId, "TestO" + organizationId, "Test Street No. " + organizationId));
			employees.add(employee);
		}
		return employees;
	}
}
