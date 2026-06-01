package com.ems.controller;

import com.ems.dto.BonusResponse;
import com.ems.dto.EmployeeRequest;
import com.ems.dto.EmployeeResponse;
import com.ems.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Employee management.
 * All endpoints (except /api/auth/**) require a valid JWT Bearer token.
 */
@RestController
@RequestMapping("/api/employees")
@Tag(name = "Employee Management", description = "CRUD operations and analytics for employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    // ── GET all (no pagination) ────────────────────────────────────────────

    @GetMapping
    @Operation(summary = "Get all employees", description = "Returns every employee as a flat list.")
    @ApiResponse(responseCode = "200", description = "List returned")
    public List<EmployeeResponse> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    // ── GET paginated with optional filters ────────────────────────────────

    /**
     * Returns a paginated list of employees.
     *
     * @param page   zero-based page index (default 0)
     * @param size   page size (default 10)
     * @param sort   sort field (default "empId")
     * @param dir    sort direction: asc | desc (default "asc")
     * @param q      optional search string (name / email)
     * @param deptId optional department filter
     * @param status optional status filter
     */
    @GetMapping("/paged")
    @Operation(summary = "Get employees paginated", description = "Supports page, size, sort, and optional filters.")
    @ApiResponse(responseCode = "200", description = "Page returned")
    public Page<EmployeeResponse> getEmployeesPaginated(
            @RequestParam(defaultValue = "0")    int page,
            @RequestParam(defaultValue = "10")   int size,
            @RequestParam(defaultValue = "empId") String sort,
            @RequestParam(defaultValue = "asc")  String dir,
            @RequestParam(required = false)      String q,
            @RequestParam(required = false)      Integer deptId,
            @RequestParam(required = false)      String status) {

        Sort.Direction direction = dir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        PageRequest pageable = PageRequest.of(page, size, Sort.by(direction, sort));
        return employeeService.getEmployeesPaginated(q, deptId, status, pageable);
    }

    // ── GET by ID ──────────────────────────────────────────────────────────

    @GetMapping("/{id}")
    @Operation(summary = "Get employee by ID")
    @ApiResponse(responseCode = "200", description = "Employee found")
    @ApiResponse(responseCode = "404", description = "Not found")
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable int id) {
        return employeeService.getEmployeeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ── POST create ────────────────────────────────────────────────────────

    @PostMapping
    @Operation(summary = "Create a new employee", description = "Accepts an EmployeeRequest DTO; returns the saved EmployeeResponse.")
    @ApiResponse(responseCode = "200", description = "Employee created")
    @ApiResponse(responseCode = "400", description = "Validation error")
    public ResponseEntity<EmployeeResponse> createEmployee(@Valid @RequestBody EmployeeRequest request) {
        return ResponseEntity.ok(employeeService.createEmployee(request));
    }

    // ── PUT update ─────────────────────────────────────────────────────────

    @PutMapping("/{id}")
    @Operation(summary = "Update an employee")
    @ApiResponse(responseCode = "200", description = "Updated")
    @ApiResponse(responseCode = "404", description = "Not found")
    public ResponseEntity<EmployeeResponse> updateEmployee(
            @PathVariable int id,
            @Valid @RequestBody EmployeeRequest request) {
        return employeeService.updateEmployee(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ── DELETE ─────────────────────────────────────────────────────────────

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an employee")
    @ApiResponse(responseCode = "204", description = "Deleted")
    public ResponseEntity<Void> deleteEmployee(@PathVariable int id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    // ── Stored Procedure: annual bonus ─────────────────────────────────────

    /**
     * Calls the {@code calculate_annual_bonus} PostgreSQL function.
     *
     * @param id        employee ID
     * @param bonusPct  bonus percentage (e.g. 10.0)
     */
    @GetMapping("/{id}/bonus")
    @Operation(summary = "Calculate annual bonus",
               description = "Calls the calculate_annual_bonus stored procedure with the given bonus percentage.")
    @ApiResponse(responseCode = "200", description = "Bonus calculated")
    @ApiResponse(responseCode = "404", description = "Employee not found")
    public ResponseEntity<BonusResponse> calculateBonus(
            @PathVariable int id,
            @RequestParam(defaultValue = "10.0") double bonusPct) {
        try {
            return ResponseEntity.ok(employeeService.calculateBonus(id, bonusPct));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
