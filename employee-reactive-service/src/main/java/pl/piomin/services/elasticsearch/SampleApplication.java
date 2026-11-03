package pl.piomin.services.elasticsearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import pl.piomin.services.elasticsearch.repository.EmployeeRepository;

@SpringBootApplication
public class SampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SampleApplication.class, args);
    }

    @Bean
    @ConditionalOnProperty("initial-import.enabled")
    public SampleDataSet dataSet(EmployeeRepository employeeRepository) {
        return new SampleDataSet(employeeRepository);
    }

}
