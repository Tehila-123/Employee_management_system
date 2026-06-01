package com.ems.service;

import com.ems.dto.BonusResponse;
import com.ems.dto.EmployeeRequest;
import com.ems.dto.EmployeeResponse;
import com.ems.model.Department;
import com.ems.model.Employee;
import com.ems.repository.DepartmentRepository;
import com.ems.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service layer for Employee business logic.
 * Maps between DTOs and entities; never exposes entities to the controller.
 */
@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    // ── Queries ────────────────────────────────────────────────────────────

    /** Returns all employees as response DTOs. */
    public List<EmployeeResponse> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Returns a paginated, optionally filtered list of employees.
     *
     * @param query  optional search string (name / email)
     * @param deptId optional department filter
     * @param status optional status filter
     * @param pageable pagination + sort parameters
     */
    public Page<EmployeeResponse> getEmployeesPaginated(
            String query, Integer deptId, String status, Pageable pageable) {

        Page<Employee> page;

        if (query != null && !query.isBlank()) {
            page = employeeRepository.search(query.trim(), pageable);
        } else if (deptId != null) {
            page = employeeRepository.findByDeptId(deptId, pageable);
        } else if (status != null && !status.isBlank()) {
            page = employeeRepository.findByStatus(status, pageable);
        } else {
            page = employeeRepository.findAll(pageable);
        }

        return page.map(this::toResponse);
    }

    public Optional<EmployeeResponse> getEmployeeById(int id) {
        return employeeRepository.findById(id).map(this::toResponse);
    }

    // ── Mutations ──────────────────────────────────────────────────────────

    /**
     * Creates a new employee from the request DTO.
     *
     * @param request validated EmployeeRequest DTO
     * @return saved employee as EmployeeResponse DTO
     */
    public EmployeeResponse createEmployee(EmployeeRequest request) {
        Employee emp = toEntity(request);
        return toResponse(employeeRepository.save(emp));
    }

    /**
     * Updates an existing employee.
     *
     * @param id      employee ID
     * @param request updated fields
     * @return updated EmployeeResponse, or empty if not found
     */
    public Optional<EmployeeResponse> updateEmployee(int id, EmployeeRequest request) {
        return employeeRepository.findById(id).map(existing -> {
            applyRequest(existing, request);
            return toResponse(employeeRepository.save(existing));
        });
    }

    public void deleteEmployee(int id) {
        employeeRepository.deleteById(id);
    }

    // ── Stored Procedure ───────────────────────────────────────────────────

    /**
     * Calls the {@code calculate_annual_bonus} PostgreSQL function.
     *
     * @param empId     employee ID
     * @param bonusPct  bonus percentage (e.g. 10.0 for 10%)
     * @return BonusResponse with computed amounts
     */
    public BonusResponse calculateBonus(int empId, double bonusPct) {
        Employee emp = employeeRepository.findById(empId)
                .orElseThrow(() -> new RuntimeException("Employee not found: " + empId));

        Double bonus = employeeRepository.calculateAnnualBonus(empId, bonusPct);
        if (bonus == null) bonus = 0.0;

        String name = emp.getFirstName() + " " + emp.getLastName();
        return new BonusResponse(empId, name, emp.getSalary(), bonus);
    }

    // ── Legacy raw-entity methods (kept for backward compat) ───────────────

    /** @deprecated Use {@link #createEmployee(EmployeeRequest)} instead. */
    @Deprecated
    public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public Optional<Employee> getEmployeeByUserId(int userId) {
        return employeeRepository.findByUserId(userId);
    }

    // ── Mapping helpers ────────────────────────────────────────────────────

    private EmployeeResponse toResponse(Employee e) {
        EmployeeResponse r = new EmployeeResponse();
        r.setEmpId(e.getEmpId());
        r.setFirstName(e.getFirstName());
        r.setLastName(e.getLastName());
        r.setEmail(e.getEmail());
        r.setPhone(e.getPhone());
        r.setJobTitle(e.getJobTitle());
        r.setDeptId(e.getDeptId());
        r.setStatus(e.getStatus());
        r.setSalary(e.getSalary());
        r.setHireDate(e.getHireDate() != null ? e.getHireDate().toString() : null);

        // Resolve department name
        if (e.getDeptId() != null) {
            departmentRepository.findById(e.getDeptId())
                    .ifPresent(d -> r.setDeptName(d.getDeptName()));
        }
        return r;
    }

    private Employee toEntity(EmployeeRequest req) {
        Employee e = new Employee();
        applyRequest(e, req);
        return e;
    }

    private void applyRequest(Employee e, EmployeeRequest req) {
        e.setFirstName(req.getFirstName());
        e.setLastName(req.getLastName());
        e.setEmail(req.getEmail());
        e.setPhone(req.getPhone());
        e.setJobTitle(req.getJobTitle());
        e.setDeptId(req.getDeptId());
        e.setStatus(req.getStatus());
        e.setSalary(req.getSalary() != null ? req.getSalary() : 0.0);
        if (req.getHireDate() != null && !req.getHireDate().isBlank()) {
            e.setHireDate(Date.valueOf(req.getHireDate()));
        }
    }
}
