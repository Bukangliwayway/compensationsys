package com.haymeb.compensationsys.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.haymeb.compensationsys.models.Compensation;

public interface CompensationRepository extends JpaRepository<Compensation, Integer> {

	@Query("SELECT c FROM Compensation c WHERE c.employee.id = :id AND c.date BETWEEN :startDate AND :endDate")
	List<Compensation> findByEmployeeIdAndDateBetween(@Param("id") int employeeId,
			@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

	@Query("SELECT c FROM Compensation c WHERE YEAR(c.date) = :year AND MONTH(c.date) = :month AND c.employee.id = :id")
	List<Compensation> findByEmployeeIdAndMonth(@Param("id") int employeeId, @Param("year") int year,
			@Param("month") int month);

	@Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END AS hasSalaryCompensation "
			+ "FROM compensations c " + "WHERE YEAR(c.date) = :year AND MONTH(c.date) = :month "
			+ "AND c.employee_id = :id AND c.type = 'Salary'", nativeQuery = true)
	Integer hasSalaryCompensationForMonth(@Param("id") int employeeId, @Param("year") int year,
			@Param("month") int month);

	@Query(value = "SELECT c.id FROM compensations c " + "WHERE YEAR(c.date) = :year AND MONTH(c.date) = :month "
			+ "AND c.employee_id = :id AND c.type = 'Salary' " + "LIMIT 1", nativeQuery = true)
	Integer findSalaryCompensationIdForMonth(@Param("id") int employeeId, @Param("year") int year,
			@Param("month") int month);
}
