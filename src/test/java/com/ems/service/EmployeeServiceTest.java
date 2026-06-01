package com.ems.service;

import com.ems.dto.BonusResponse;
import com.ems.dto.EmployeeRequest;
import com.ems.dto.EmployeeResponse;
import com.ems.model.Department;
import com.ems.model.Employee;
import com.ems.repository.DepartmentRepository;
import com.ems.repository.EmployeeRepository;
import com.ems.util.TestLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, TestLogger.class})
class EmployeeServiceTest {

    @Mock private EmployeeRepository   employeeRepository;
    @Mock private DepartmentRepository departmentRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee sampleEmployee;
    private Department sampleDept;

    private static final String EMAIL = "tehilawavumbuzi@gmail.com";

    @BeforeEach
    void setUp() {
        sampleDept = new Department();
        sampleDept.setDeptId(1);
        sampleDept.setDeptName("Engineering");

        sampleEmployee = new Employee();
        sampleEmployee.setEmpId(1);
        sampleEmployee.setFirstName("Tehila");
        sampleEmployee.setLastName("Wavumbuzi");
        sampleEmployee.setEmail(EMAIL);
        sampleEmployee.setJobTitle("Senior Engineer");
        sampleEmployee.setDeptId(1);
        sampleEmployee.setStatus("active");
        sampleEmployee.setSalary(5000.0);
        sampleEmployee.setHireDate(Date.valueOf("2023-01-15"));
    }

    // ── getAllEmployees ─────────────────────────────────────────────────────

    @Test
    @DisplayName("getAllEmployees: returns mapped DTOs for every employee")
    void getAllEmployees_returnsMappedList() {
        when(employeeRepository.findAll()).thenReturn(List.of(sampleEmployee));
        when(departmentRepository.findById(1)).thenReturn(Optional.of(sampleDept));

        List<EmployeeResponse> result = employeeService.getAllEmployees();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("Tehila");
        assertThat(result.get(0).getEmail()).isEqualTo(EMAIL);
        assertThat(result.get(0).getDeptName()).isEqualTo("Engineering");
        verify(employeeRepository, times(1)).findAll();
    }

    // ── getEmployeeById ────────────────────────────────────────────────────

    @Test
    @DisplayName("getEmployeeById: found → returns DTO with correct email")
    void getEmployeeById_found() {
        when(employeeRepository.findById(1)).thenReturn(Optional.of(sampleEmployee));
        when(departmentRepository.findById(1)).thenReturn(Optional.of(sampleDept));

        Optional<EmployeeResponse> result = employeeService.getEmployeeById(1);

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo(EMAIL);
    }

    @Test
    @DisplayName("getEmployeeById: not found → returns empty")
    void getEmployeeById_notFound() {
        when(employeeRepository.findById(99)).thenReturn(Optional.empty());

        assertThat(employeeService.getEmployeeById(99)).isEmpty();
    }

    // ── createEmployee ─────────────────────────────────────────────────────

    @Test
    @DisplayName("createEmployee: saves entity and returns DTO with " + EMAIL)
    void createEmployee_savesAndReturns() {
        EmployeeRequest req = new EmployeeRequest();
        req.setFirstName("Tehila");
        req.setLastName("Wavumbuzi");
        req.setEmail(EMAIL);
        req.setJobTitle("Backend Engineer");
        req.setDeptId(1);
        req.setStatus("onboarding");
        req.setSalary(4500.0);
        req.setHireDate("2026-06-01");

        when(employeeRepository.save(any(Employee.class))).thenAnswer(inv -> {
            Employee e = inv.getArgument(0);
            e.setEmpId(42);
            return e;
        });
        when(departmentRepository.findById(1)).thenReturn(Optional.of(sampleDept));

        EmployeeResponse result = employeeService.createEmployee(req);

        assertThat(result.getEmpId()).isEqualTo(42);
        assertThat(result.getEmail()).isEqualTo(EMAIL);
        assertThat(result.getStatus()).isEqualTo("onboarding");
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    // ── updateEmployee ─────────────────────────────────────────────────────

    @Test
    @DisplayName("updateEmployee: applies changes when employee exists")
    void updateEmployee_appliesChanges() {
        EmployeeRequest req = new EmployeeRequest();
        req.setFirstName("Tehila");
        req.setLastName("Wavumbuzi");
        req.setEmail(EMAIL);
        req.setJobTitle("Staff Engineer");
        req.setDeptId(1);
        req.setStatus("active");
        req.setSalary(6000.0);

        when(employeeRepository.findById(1)).thenReturn(Optional.of(sampleEmployee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(sampleEmployee);
        when(departmentRepository.findById(1)).thenReturn(Optional.of(sampleDept));

        Optional<EmployeeResponse> result = employeeService.updateEmployee(1, req);

        assertThat(result).isPresent();
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    @DisplayName("updateEmployee: not found → returns empty, no save")
    void updateEmployee_notFound() {
        when(employeeRepository.findById(99)).thenReturn(Optional.empty());

        Optional<EmployeeResponse> result = employeeService.updateEmployee(99, new EmployeeRequest());

        assertThat(result).isEmpty();
        verify(employeeRepository, never()).save(any());
    }

    // ── deleteEmployee ─────────────────────────────────────────────────────

    @Test
    @DisplayName("deleteEmployee: calls repository deleteById")
    void deleteEmployee_callsRepository() {
        doNothing().when(employeeRepository).deleteById(1);

        employeeService.deleteEmployee(1);

        verify(employeeRepository, times(1)).deleteById(1);
    }

    // ── calculateBonus ─────────────────────────────────────────────────────

    @Test
    @DisplayName("calculateBonus: 10% of 5000 → bonus=500, annual=60500")
    void calculateBonus_returnsCorrectValues() {
        when(employeeRepository.findById(1)).thenReturn(Optional.of(sampleEmployee));
        when(employeeRepository.calculateAnnualBonus(1, 10.0)).thenReturn(500.0);

        BonusResponse result = employeeService.calculateBonus(1, 10.0);

        assertThat(result.getBonusAmount()).isEqualTo(500.0);
        assertThat(result.getBaseSalary()).isEqualTo(5000.0);
        assertThat(result.getAnnualTotal()).isEqualTo((5000.0 * 12) + 500.0);
        assertThat(result.getEmployeeName()).isEqualTo("Tehila Wavumbuzi");
    }

    @Test
    @DisplayName("calculateBonus: employee not found → throws RuntimeException")
    void calculateBonus_throwsWhenNotFound() {
        when(employeeRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.calculateBonus(99, 10.0))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("99");
    }

    // ── Pagination ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("getEmployeesPaginated: with query → delegates to search()")
    void getEmployeesPaginated_withQuery() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Employee> mockPage = new PageImpl<>(List.of(sampleEmployee));

        when(employeeRepository.search("tehila", pageable)).thenReturn(mockPage);
        when(departmentRepository.findById(1)).thenReturn(Optional.of(sampleDept));

        Page<EmployeeResponse> result = employeeService.getEmployeesPaginated("tehila", null, null, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getEmail()).isEqualTo(EMAIL);
        verify(employeeRepository).search("tehila", pageable);
        verify(employeeRepository, never()).findAll(pageable);
    }
}
