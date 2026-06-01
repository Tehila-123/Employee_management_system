package com.ems.controller;

import com.ems.model.Department;
import com.ems.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

@RestController
@RequestMapping("/api/departments")
@Tag(name = "Department Management", description = "Endpoints for managing departments")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @GetMapping
    @Operation(summary = "Get all departments", description = "Retrieves a list of all departments.")
    @ApiResponse(responseCode = "200", description = "List of departments retrieved")
    public List<Department> getAllDepartments() {
        return departmentService.getAllDepartments();
    }

    @GetMapping("/{id}/count")
    @Operation(summary = "Get employee count by department ID", description = "Retrieves the number of employees in a department using a stored routine.")
    @ApiResponse(responseCode = "200", description = "Count retrieved")
    public ResponseEntity<Integer> getEmployeeCountByDept(@PathVariable int id) {
        return ResponseEntity.ok(departmentService.getEmployeeCountByDept(id));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get department by ID", description = "Retrieves a single department record by its ID.")
    @ApiResponse(responseCode = "200", description = "Department found")
    @ApiResponse(responseCode = "404", description = "Department not found")
    public ResponseEntity<Department> getDepartmentById(@PathVariable int id) {
        return departmentService.getDepartmentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create a new department", description = "Adds a new department to the system.")
    @ApiResponse(responseCode = "200", description = "Department created")
    public Department createDepartment(@RequestBody Department department) {
        return departmentService.saveDepartment(department);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a department", description = "Updates an existing department record.")
    @ApiResponse(responseCode = "200", description = "Department updated")
    @ApiResponse(responseCode = "404", description = "Department not found")
    public ResponseEntity<Department> updateDepartment(@PathVariable int id, @RequestBody Department department) {
        return departmentService.getDepartmentById(id)
                .map(existingDept -> {
                    department.setDeptId(id);
                    return ResponseEntity.ok(departmentService.saveDepartment(department));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a department", description = "Removes a department record from the system.")
    @ApiResponse(responseCode = "204", description = "Department deleted")
    public ResponseEntity<Void> deleteDepartment(@PathVariable int id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }
}
