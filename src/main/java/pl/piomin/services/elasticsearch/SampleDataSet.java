package pl.piomin.services.elasticsearch;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.client.reactive.ReactiveElasticsearchClient;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import pl.piomin.services.elasticsearch.model.Department;
import pl.piomin.services.elasticsearch.model.Employee;
import pl.piomin.services.elasticsearch.model.Organization;
import pl.piomin.services.elasticsearch.repository.EmployeeRepository;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SampleDataSet {

    private static final Logger LOGGER = LoggerFactory.getLogger(SampleDataSet.class);
    private static final String INDEX_NAME = "sample";
    private static final String INDEX_TYPE = "employee";

    @Autowired
    EmployeeRepository repository;
    @Autowired
    ReactiveElasticsearchTemplate template;
    @Autowired
    ReactiveElasticsearchClient client;


    @PostConstruct
    public void init() {
        for (int i = 0; i < 1; i++) {
            bulk(i);
        }
    }

    public void bulk(int ii) {
        try {
            Mono<Boolean> exists = client.indices().existsIndex(request -> request.indices(INDEX_NAME));
            exists.subscribe(ex -> {
                if (!ex) {
                    LOGGER.info("Creating index: {}", INDEX_NAME);
                    client.indices().createIndex(request -> request.index(INDEX_NAME));
                }
            });
            ObjectMapper mapper = new ObjectMapper();
            List<IndexQuery> queries = new ArrayList<>();
            List<Employee> employees = employees();
//            for (Employee employee : employees) {
//                IndexQuery indexQuery = new IndexQuery();
//                indexQuery.setId(employee.getId().toString());
//                indexQuery.setSource(mapper.writeValueAsString(employee));
//                indexQuery.setIndexName(INDEX_NAME);
//                indexQuery.setType(INDEX_TYPE);
//                queries.add(indexQuery);
//            }
//            if (queries.size() > 0) {
//                template.bulkIndex(queries);
//            }
            repository.saveAll(employees).subscribe(empl -> LOGGER.info("ADD: {}", empl));
//            client.indices().refreshIndex(refreshRequest -> refreshRequest.indices(INDEX_NAME)).block();
//            template.refresh(INDEX_NAME);
            LOGGER.info("BulkIndex completed: {}", ii);
        } catch (Exception e) {
            LOGGER.error("Error bulk index", e);
        }
    }

    private List<Employee> employees() {
        List<Employee> employees = new ArrayList<>();
        int id = repository.count().block().intValue();
        LOGGER.info("Starting from id: {}", id);
        for (int i = id; i < 10000 + id; i++) {
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
