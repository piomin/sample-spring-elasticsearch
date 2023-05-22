package pl.piomin.services.elasticsearch;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import pl.piomin.services.elasticsearch.model.Department;
import pl.piomin.services.elasticsearch.model.Employee;
import pl.piomin.services.elasticsearch.model.Organization;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SampleDataSet {

    private static final Logger LOGGER = LoggerFactory.getLogger(SampleDataSet.class);
    private static final String INDEX_NAME = "sample";
    private static final String INDEX_TYPE = "employee";
    private static int COUNTER = 0;

    @Autowired
    IndexOperations indexOperations;
    @Autowired
    ElasticsearchOperations template;
    @Autowired
    TaskExecutor taskExecutor;

    @PostConstruct
    public void init() {
        if (!indexOperations.exists()) {
            indexOperations.create();
            LOGGER.info("New index created: {}", INDEX_NAME);
        }
        for (int i = 0; i < 10000; i++) {
            taskExecutor.execute(() -> bulk());
        }
    }

    public void bulk() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<IndexQuery> queries = new ArrayList<>();
            List<Employee> employees = employees();
            for (Employee employee : employees) {
                IndexQuery indexQuery = new IndexQuery();
                indexQuery.setSource(mapper.writeValueAsString(employee));
                // TODO - think about it
//                indexQuery.setIndexName(INDEX_NAME);
                queries.add(indexQuery);
            }
            if (queries.size() > 0) {
                template.bulkIndex(queries, Employee.class);
            }
            // TODO - replace it
//            template.refresh(INDEX_NAME);
            LOGGER.info("BulkIndex completed: {}", ++COUNTER);
        } catch (Exception e) {
            LOGGER.error("Error bulk index", e);
        }
    }

    private List<Employee> employees() {
        List<Employee> employees = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            Random r = new Random();
            Employee employee = new Employee();
            employee.setName("JohnSmith" + r.nextInt(1000000));
            employee.setAge(r.nextInt(100));
            employee.setPosition("Developer");
            int departmentId = r.nextInt(500000);
            employee.setDepartment(new Department((long) departmentId, "TestD" + departmentId));
            int organizationId = departmentId / 100;
            employee.setOrganization(new Organization((long) organizationId, "TestO" + organizationId, "Test Street No. " + organizationId));
            employees.add(employee);
        }
        return employees;
    }

}
