package com.ems.repository;

import com.ems.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {
    
    @Query(value = "SELECT get_employee_count_by_dept(:deptId)", nativeQuery = true)
    Integer getEmployeeCountByDept(@Param("deptId") Integer deptId);
}
