package com.ems.service;

import com.ems.model.LeaveRequest;
import com.ems.repository.LeaveRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LeaveService {

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    public List<LeaveRequest> getAllLeaveRequests() {
        return leaveRequestRepository.findAll();
    }

    public List<LeaveRequest> getLeaveRequestsByEmployee(int empId) {
        return leaveRequestRepository.findByEmpId(empId);
    }

    public Optional<LeaveRequest> getLeaveRequestById(int id) {
        return leaveRequestRepository.findById(id);
    }

    public LeaveRequest saveLeaveRequest(LeaveRequest leaveRequest) {
        return leaveRequestRepository.save(leaveRequest);
    }

    public void deleteLeaveRequest(int id) {
        leaveRequestRepository.deleteById(id);
    }
}
