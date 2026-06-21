package in.trendsnag.user_management.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.stereotype.Service;

import in.trendsnag.user_management.model.Employee;
import in.trendsnag.user_management.repository.EmployeeRepository;

@Service
public class EmployeeService {

	@Autowired
	private EmployeeRepository employeeRepository;

    public List<Employee> getEmployeesAboveAge(int age) {
        return employeeRepository.findByAgeGreaterThan(age);
    }
    
    @Autowired
    private MongoTemplate mongoTemplate;

    public List<Employee> getEmployeesWithDept() {

        Aggregation agg = Aggregation.newAggregation(
            Aggregation.lookup("departments", "deptId", "_id", "deptInfo"),
            Aggregation.unwind("deptInfo"),
            Aggregation.project("name", "age")
                .and("deptInfo.deptName").as("department")
        );

        return mongoTemplate.aggregate(agg, "employees", Employee.class)
                            .getMappedResults();
    }
}
