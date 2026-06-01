package com.ems.controller;

import com.ems.dto.EmployeeResponse;
import com.ems.security.JwtAuthenticationFilter;
import com.ems.security.JwtUtils;
import com.ems.security.UserDetailsServiceImpl;
import com.ems.service.EmployeeService;
import com.ems.util.TestLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(TestLogger.class)
class EmployeeControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private JwtUtils                jwtUtils;
    @MockBean private UserDetailsServiceImpl  userDetailsService;
    @MockBean private JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockBean private EmployeeService         employeeService;

    private static final String EMAIL = "tehilawavumbuzi@gmail.com";

    private EmployeeResponse sample;

    @BeforeEach
    void setUp() {
        sample = new EmployeeResponse();
        sample.setEmpId(1);
        sample.setFirstName("Tehila");
        sample.setLastName("Wavumbuzi");
        sample.setEmail(EMAIL);
        sample.setJobTitle("Senior Engineer");
        sample.setDeptName("Engineering");
        sample.setStatus("active");
        sample.setSalary(5000.0);
    }

    @Test
    @DisplayName("GET /api/employees → 200 with list of EmployeeResponse DTOs")
    void getAllEmployees_returns200() throws Exception {
        when(employeeService.getAllEmployees()).thenReturn(List.of(sample));

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Tehila"))
                .andExpect(jsonPath("$[0].email").value(EMAIL))
                .andExpect(jsonPath("$[0].deptName").value("Engineering"));
    }

    @Test
    @DisplayName("GET /api/employees/paged → 200 with paginated content")
    void getEmployeesPaginated_returns200() throws Exception {
        Page<EmployeeResponse> page = new PageImpl<>(List.of(sample));

        when(employeeService.getEmployeesPaginated(
                isNull(), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/employees/paged?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].email").value(EMAIL))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("GET /api/employees/1 → 200 with correct employee")
    void getEmployeeById_found_returns200() throws Exception {
        when(employeeService.getEmployeeById(1)).thenReturn(Optional.of(sample));

        mockMvc.perform(get("/api/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(EMAIL))
                .andExpect(jsonPath("$.salary").value(5000.0));
    }

    @Test
    @DisplayName("GET /api/employees/99 → 404 when not found")
    void getEmployeeById_notFound_returns404() throws Exception {
        when(employeeService.getEmployeeById(99)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/employees/99"))
                .andExpect(status().isNotFound());
    }
}
