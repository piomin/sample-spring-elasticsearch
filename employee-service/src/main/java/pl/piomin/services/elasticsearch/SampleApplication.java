package pl.piomin.services.elasticsearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;

import pl.piomin.services.elasticsearch.model.Employee;

import java.util.concurrent.Executors;

@SpringBootApplication
public class SampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SampleApplication.class, args);
    }

    @Bean
    @ConditionalOnProperty("initial-import.enabled")
    public SampleDataSet dataSet(ElasticsearchOperations elasticsearchOperations, TaskExecutor taskExecutor) {
        IndexOperations indexOperations = elasticsearchOperations.indexOps(Employee.class);
        return new SampleDataSet(indexOperations, elasticsearchOperations, taskExecutor);
    }

    @Bean(name = "ConcurrentTaskExecutor")
    public TaskExecutor taskExecutor () {
        return new ConcurrentTaskExecutor(Executors.newFixedThreadPool(3));
    }

}
