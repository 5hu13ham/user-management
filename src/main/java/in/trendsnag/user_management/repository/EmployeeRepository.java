package in.trendsnag.user_management.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import in.trendsnag.user_management.model.Employee;

public interface EmployeeRepository extends MongoRepository <Employee, String>{
	
	List<Employee> findByAgeGreaterThan(int age);
}
