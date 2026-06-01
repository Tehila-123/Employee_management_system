package com.ems.service;

import com.ems.model.LeaveRequest;
import com.ems.repository.LeaveRequestRepository;
import com.ems.util.TestLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, TestLogger.class})
class LeaveServiceTest {

    @Mock
    private LeaveRequestRepository leaveRequestRepository;

    @InjectMocks
    private LeaveService leaveService;

    private LeaveRequest sampleLeave;

    @BeforeEach
    void setUp() {
        sampleLeave = new LeaveRequest();
        sampleLeave.setLeaveId(1);
        sampleLeave.setEmpId(5);
        sampleLeave.setLeaveType("PTO");
        sampleLeave.setStartDate(Date.valueOf("2026-06-01"));
        sampleLeave.setEndDate(Date.valueOf("2026-06-05"));
        sampleLeave.setStatus("pending");
        sampleLeave.setReason("Vacation – tehilawavumbuzi@gmail.com");
    }

    @Test
    @DisplayName("getAllLeaveRequests: returns all records")
    void getAllLeaveRequests_returnsAll() {
        when(leaveRequestRepository.findAll()).thenReturn(List.of(sampleLeave));

        List<LeaveRequest> result = leaveService.getAllLeaveRequests();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLeaveType()).isEqualTo("PTO");
        verify(leaveRequestRepository).findAll();
    }

    @Test
    @DisplayName("getAllLeaveRequests: empty table → returns empty list")
    void getAllLeaveRequests_empty() {
        when(leaveRequestRepository.findAll()).thenReturn(List.of());

        assertThat(leaveService.getAllLeaveRequests()).isEmpty();
    }

    @Test
    @DisplayName("getLeaveRequestsByEmployee: returns only employee 5's requests")
    void getLeaveRequestsByEmployee_returnsFiltered() {
        when(leaveRequestRepository.findByEmpId(5)).thenReturn(List.of(sampleLeave));

        List<LeaveRequest> result = leaveService.getLeaveRequestsByEmployee(5);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmpId()).isEqualTo(5);
    }

    @Test
    @DisplayName("getLeaveRequestsByEmployee: unknown employee → returns empty")
    void getLeaveRequestsByEmployee_unknownEmp_returnsEmpty() {
        when(leaveRequestRepository.findByEmpId(99)).thenReturn(List.of());

        assertThat(leaveService.getLeaveRequestsByEmployee(99)).isEmpty();
    }

    @Test
    @DisplayName("getLeaveRequestById: found → returns present with status=pending")
    void getLeaveRequestById_found() {
        when(leaveRequestRepository.findById(1)).thenReturn(Optional.of(sampleLeave));

        Optional<LeaveRequest> result = leaveService.getLeaveRequestById(1);

        assertThat(result).isPresent();
        assertThat(result.get().getStatus()).isEqualTo("pending");
    }

    @Test
    @DisplayName("getLeaveRequestById: not found → returns empty")
    void getLeaveRequestById_notFound() {
        when(leaveRequestRepository.findById(99)).thenReturn(Optional.empty());

        assertThat(leaveService.getLeaveRequestById(99)).isEmpty();
    }

    @Test
    @DisplayName("saveLeaveRequest: persists and returns the record")
    void saveLeaveRequest_savesAndReturns() {
        when(leaveRequestRepository.save(sampleLeave)).thenReturn(sampleLeave);

        LeaveRequest result = leaveService.saveLeaveRequest(sampleLeave);

        assertThat(result.getLeaveId()).isEqualTo(1);
        verify(leaveRequestRepository).save(sampleLeave);
    }

    @Test
    @DisplayName("saveLeaveRequest: status updated to approved")
    void saveLeaveRequest_updatesStatus() {
        sampleLeave.setStatus("approved");
        when(leaveRequestRepository.save(sampleLeave)).thenReturn(sampleLeave);

        LeaveRequest result = leaveService.saveLeaveRequest(sampleLeave);

        assertThat(result.getStatus()).isEqualTo("approved");
    }

    @Test
    @DisplayName("deleteLeaveRequest: calls repository deleteById")
    void deleteLeaveRequest_callsRepository() {
        doNothing().when(leaveRequestRepository).deleteById(1);

        leaveService.deleteLeaveRequest(1);

        verify(leaveRequestRepository).deleteById(1);
    }
}
