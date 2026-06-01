package com.ems.controller;

import com.ems.model.LeaveRequest;
import com.ems.service.LeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

@RestController
@RequestMapping("/api/leaves")
@Tag(name = "Leave Management", description = "Endpoints for managing employee leave requests")
public class LeaveController {

    @Autowired
    private LeaveService leaveService;

    @GetMapping
    @Operation(summary = "Get all leave requests", description = "Retrieves a list of all leave requests.")
    @ApiResponse(responseCode = "200", description = "List of leave requests retrieved")
    public List<LeaveRequest> getAllLeaves() {
        return leaveService.getAllLeaveRequests();
    }

    @GetMapping("/employee/{empId}")
    @Operation(summary = "Get leave requests by employee ID", description = "Retrieves all leave requests for a specific employee.")
    @ApiResponse(responseCode = "200", description = "List of leave requests retrieved")
    public List<LeaveRequest> getLeavesByEmployee(@PathVariable int empId) {
        return leaveService.getLeaveRequestsByEmployee(empId);
    }

    @PostMapping
    @Operation(summary = "Submit a leave request", description = "Submits a new leave request.")
    @ApiResponse(responseCode = "200", description = "Leave request submitted")
    public LeaveRequest requestLeave(@RequestBody LeaveRequest leaveRequest) {
        return leaveService.saveLeaveRequest(leaveRequest);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update leave request status", description = "Approves or rejects a leave request.")
    @ApiResponse(responseCode = "200", description = "Status updated")
    @ApiResponse(responseCode = "404", description = "Leave request not found")
    public ResponseEntity<LeaveRequest> updateLeaveStatus(@PathVariable int id, @RequestParam String status) {
        return leaveService.getLeaveRequestById(id)
                .map(leave -> {
                    leave.setStatus(status);
                    return ResponseEntity.ok(leaveService.saveLeaveRequest(leave));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
