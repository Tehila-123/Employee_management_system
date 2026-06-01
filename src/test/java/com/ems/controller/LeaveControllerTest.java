package com.ems.controller;

import com.ems.model.LeaveRequest;
import com.ems.security.JwtAuthenticationFilter;
import com.ems.security.JwtUtils;
import com.ems.security.UserDetailsServiceImpl;
import com.ems.service.LeaveService;
import com.ems.util.TestLogger;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LeaveController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(TestLogger.class)
class LeaveControllerTest {

    @Autowired private MockMvc       mockMvc;
    @Autowired private ObjectMapper  objectMapper;

    @MockBean private JwtUtils                jwtUtils;
    @MockBean private UserDetailsServiceImpl  userDetailsService;
    @MockBean private JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockBean private LeaveService            leaveService;

    private static final String EMAIL = "tehilawavumbuzi@gmail.com";

    private LeaveRequest sample;

    @BeforeEach
    void setUp() {
        sample = new LeaveRequest();
        sample.setLeaveId(1);
        sample.setEmpId(5);
        sample.setLeaveType("PTO");
        sample.setStartDate(Date.valueOf("2026-06-01"));
        sample.setEndDate(Date.valueOf("2026-06-05"));
        sample.setStatus("pending");
        sample.setReason("Vacation requested by " + EMAIL);
    }

    @Test
    @DisplayName("GET /api/leaves → 200 with all leave requests")
    void getAllLeaves_returns200() throws Exception {
        when(leaveService.getAllLeaveRequests()).thenReturn(List.of(sample));

        mockMvc.perform(get("/api/leaves"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].leaveType").value("PTO"))
                .andExpect(jsonPath("$[0].status").value("pending"));
    }

    @Test
    @DisplayName("GET /api/leaves/employee/5 → 200 with employee's requests")
    void getLeavesByEmployee_returns200() throws Exception {
        when(leaveService.getLeaveRequestsByEmployee(5)).thenReturn(List.of(sample));

        mockMvc.perform(get("/api/leaves/employee/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].empId").value(5));
    }

    @Test
    @DisplayName("POST /api/leaves → 200 creates leave request for " + EMAIL)
    void requestLeave_returns200() throws Exception {
        when(leaveService.saveLeaveRequest(any(LeaveRequest.class))).thenReturn(sample);

        mockMvc.perform(post("/api/leaves")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sample)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.leaveType").value("PTO"));
    }

    @Test
    @DisplayName("PUT /api/leaves/1/status?status=approved → 200 updated")
    void updateLeaveStatus_approved_returns200() throws Exception {
        sample.setStatus("approved");
        when(leaveService.getLeaveRequestById(1)).thenReturn(Optional.of(sample));
        when(leaveService.saveLeaveRequest(any())).thenReturn(sample);

        mockMvc.perform(put("/api/leaves/1/status").param("status", "approved"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("approved"));
    }

    @Test
    @DisplayName("PUT /api/leaves/99/status → 404 when leave not found")
    void updateLeaveStatus_notFound_returns404() throws Exception {
        when(leaveService.getLeaveRequestById(99)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/leaves/99/status").param("status", "approved"))
                .andExpect(status().isNotFound());
    }
}
