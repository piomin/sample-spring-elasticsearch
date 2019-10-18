package pl.piomin.services.elasticsearch.controller;

import java.util.List;

import pl.piomin.services.elasticsearch.model.Employee;
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

	@GetMapping("/organization/{organizationName}")
	public Flux<Employee> findByOrganizationName(@PathVariable("organizationName") String organizationName) {
		return repository.findByOrganizationName(organizationName);
	}

}
