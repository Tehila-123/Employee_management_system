package com.ems.service;

import com.ems.model.Department;
import com.ems.repository.DepartmentRepository;
import com.ems.util.TestLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, TestLogger.class})
class DepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private DepartmentService departmentService;

    private Department engineering;

    @BeforeEach
    void setUp() {
        engineering = new Department();
        engineering.setDeptId(1);
        engineering.setDeptName("Engineering");
        engineering.setManagerId(2);
    }

    @Test
    @DisplayName("getAllDepartments: returns all departments")
    void getAllDepartments_returnsAll() {
        when(departmentRepository.findAll()).thenReturn(List.of(engineering));

        List<Department> result = departmentService.getAllDepartments();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDeptName()).isEqualTo("Engineering");
        verify(departmentRepository).findAll();
    }

    @Test
    @DisplayName("getAllDepartments: empty → returns empty list")
    void getAllDepartments_empty() {
        when(departmentRepository.findAll()).thenReturn(List.of());

        assertThat(departmentService.getAllDepartments()).isEmpty();
    }

    @Test
    @DisplayName("getDepartmentById: found → returns Engineering")
    void getDepartmentById_found() {
        when(departmentRepository.findById(1)).thenReturn(Optional.of(engineering));

        Optional<Department> result = departmentService.getDepartmentById(1);

        assertThat(result).isPresent();
        assertThat(result.get().getDeptName()).isEqualTo("Engineering");
    }

    @Test
    @DisplayName("getDepartmentById: not found → returns empty")
    void getDepartmentById_notFound() {
        when(departmentRepository.findById(99)).thenReturn(Optional.empty());

        assertThat(departmentService.getDepartmentById(99)).isEmpty();
    }

    @Test
    @DisplayName("getEmployeeCountByDept: stored procedure returns 7 for dept 1")
    void getEmployeeCountByDept_returnsCount() {
        when(departmentRepository.getEmployeeCountByDept(1)).thenReturn(7);

        assertThat(departmentService.getEmployeeCountByDept(1)).isEqualTo(7);
        verify(departmentRepository).getEmployeeCountByDept(1);
    }

    @Test
    @DisplayName("getEmployeeCountByDept: empty department → returns 0")
    void getEmployeeCountByDept_zero() {
        when(departmentRepository.getEmployeeCountByDept(5)).thenReturn(0);

        assertThat(departmentService.getEmployeeCountByDept(5)).isEqualTo(0);
    }

    @Test
    @DisplayName("saveDepartment: persists and returns Engineering")
    void saveDepartment_savesAndReturns() {
        when(departmentRepository.save(engineering)).thenReturn(engineering);

        Department result = departmentService.saveDepartment(engineering);

        assertThat(result.getDeptId()).isEqualTo(1);
        assertThat(result.getDeptName()).isEqualTo("Engineering");
        verify(departmentRepository).save(engineering);
    }

    @Test
    @DisplayName("saveDepartment: new department gets assigned ID=2")
    void saveDepartment_newDepartment() {
        Department design = new Department();
        design.setDeptName("Design");

        when(departmentRepository.save(design)).thenAnswer(inv -> {
            Department d = inv.getArgument(0);
            d.setDeptId(2);
            return d;
        });

        Department result = departmentService.saveDepartment(design);

        assertThat(result.getDeptId()).isEqualTo(2);
        assertThat(result.getDeptName()).isEqualTo("Design");
    }

    @Test
    @DisplayName("deleteDepartment: calls repository deleteById")
    void deleteDepartment_callsRepository() {
        doNothing().when(departmentRepository).deleteById(1);

        departmentService.deleteDepartment(1);

        verify(departmentRepository).deleteById(1);
    }
}
