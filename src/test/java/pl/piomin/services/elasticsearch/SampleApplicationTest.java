package pl.piomin.services.elasticsearch;

import org.springframework.boot.SpringApplication;

public class SampleApplicationTest {

    public static void main(String[] args) {
        SpringApplication.from(SampleApplication::main)
                .with(ElasticsearchContainerDevMode.class)
                .run(args);
    }
}
