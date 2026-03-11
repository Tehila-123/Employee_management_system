package com.ems.dao;

import com.ems.model.LeaveRequest;
import com.ems.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LeaveDAO {
    public void createLeaveRequest(LeaveRequest request) throws SQLException {
        String sql = "INSERT INTO leave_requests (emp_id, leave_type, start_date, end_date, reason) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, request.getEmpId());
            stmt.setString(2, request.getLeaveType());
            stmt.setDate(3, request.getStartDate());
            stmt.setDate(4, request.getEndDate());
            stmt.setString(5, request.getReason());
            stmt.executeUpdate();
        }
    }

    public List<LeaveRequest> getLeaveRequestsByEmployee(int empId) throws SQLException {
        List<LeaveRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM leave_requests WHERE emp_id = ? ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, empId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    requests.add(mapResultSetToLeaveRequest(rs));
                }
            }
        }
        return requests;
    }

    public List<LeaveRequest> getAllPendingRequests() throws SQLException {
        List<LeaveRequest> requests = new ArrayList<>();
        String sql = "SELECT lr.*, e.first_name, e.last_name FROM leave_requests lr JOIN employees e ON lr.emp_id = e.emp_id WHERE lr.status = 'Pending'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                LeaveRequest lr = mapResultSetToLeaveRequest(rs);
                lr.setEmpName(rs.getString("first_name") + " " + rs.getString("last_name"));
                requests.add(lr);
            }
        }
        return requests;
    }

    public void updateLeaveStatus(int leaveId, String status, int approvedBy) throws SQLException {
        String sql = "UPDATE leave_requests SET status = ?, approved_by = ? WHERE leave_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, approvedBy);
            stmt.setInt(3, leaveId);
            stmt.executeUpdate();
        }
    }

    private LeaveRequest mapResultSetToLeaveRequest(ResultSet rs) throws SQLException {
        LeaveRequest lr = new LeaveRequest();
        lr.setLeaveId(rs.getInt("leave_id"));
        lr.setEmpId(rs.getInt("emp_id"));
        lr.setLeaveType(rs.getString("leave_type"));
        lr.setStartDate(rs.getDate("start_date"));
        lr.setEndDate(rs.getDate("end_date"));
        lr.setReason(rs.getString("reason"));
        lr.setStatus(rs.getString("status"));
        lr.setApprovedBy(rs.getInt("approved_by"));
        lr.setCreatedAt(rs.getTimestamp("created_at"));
        return lr;
    }
}

