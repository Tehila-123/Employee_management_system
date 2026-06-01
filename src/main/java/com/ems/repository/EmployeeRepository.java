package com.ems.repository;

import com.ems.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Employee entity.
 * Extends JpaRepository for standard CRUD and PagingAndSortingRepository for pagination.
 */
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    Optional<Employee> findByUserId(int userId);

    Optional<Employee> findByEmail(String email);

    /** Search by first/last name or email (case-insensitive). */
    @Query("SELECT e FROM Employee e WHERE " +
           "LOWER(e.firstName) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(e.lastName)  LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(e.email)     LIKE LOWER(CONCAT('%', :q, '%'))")
    Page<Employee> search(@Param("q") String query, Pageable pageable);

    /** Filter by department. */
    Page<Employee> findByDeptId(Integer deptId, Pageable pageable);

    /** Filter by status. */
    Page<Employee> findByStatus(String status, Pageable pageable);

    /**
     * Calls the PostgreSQL stored procedure {@code calculate_annual_bonus(emp_id, bonus_pct)}
     * and returns the computed bonus amount.
     *
     * <p>SQL to create the procedure:
     * <pre>
     * CREATE OR REPLACE FUNCTION calculate_annual_bonus(p_emp_id INT, p_bonus_pct NUMERIC)
     * RETURNS NUMERIC AS $$
     * DECLARE v_salary NUMERIC;
     * BEGIN
     *   SELECT salary INTO v_salary FROM employees WHERE emp_id = p_emp_id;
     *   RETURN v_salary * p_bonus_pct / 100.0;
     * END;
     * $$ LANGUAGE plpgsql;
     * </pre>
     */
    @Query(value = "SELECT calculate_annual_bonus(:empId, :bonusPct)", nativeQuery = true)
    Double calculateAnnualBonus(@Param("empId") int empId, @Param("bonusPct") double bonusPct);
}
