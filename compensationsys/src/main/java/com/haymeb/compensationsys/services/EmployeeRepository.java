package com.haymeb.compensationsys.services;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.haymeb.compensationsys.models.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

	@Query("SELECT e FROM Employee e WHERE "
			+ "(:firstName = '' OR LOWER(e.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))) AND "
			+ "(:lastName = '' OR LOWER(e.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))) AND "
			+ "(:position = '' OR LOWER(e.position) LIKE LOWER(CONCAT('%', :position, '%'))) ORDER BY e.id DESC")
	List<Employee> searchEmployees(@Param("firstName") String firstName, @Param("lastName") String lastName,
			@Param("position") String position);

	@Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM Employee e " + "WHERE e.firstName = :firstName "
			+ "AND e.lastName = :lastName " + "AND e.birthDate = :birthDate")
	Boolean duplicateEmployees(@Param("firstName") String firstName, @Param("lastName") String lastName,
			@Param("birthDate") Date birthDate);
}
