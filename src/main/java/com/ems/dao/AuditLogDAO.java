package com.ems.dao;

import com.ems.model.AuditLog;
import com.ems.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuditLogDAO {
    public void createLog(int userId, String action, String description, String ipAddress) throws SQLException {
        String sql = "INSERT INTO audit_logs (user_id, action, description, ip_address) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (userId > 0) stmt.setInt(1, userId); else stmt.setNull(1, java.sql.Types.INTEGER);
            stmt.setString(2, action);
            stmt.setString(3, description);
            stmt.setString(4, ipAddress);
            stmt.executeUpdate();
        }
    }

    public List<AuditLog> getAllLogs() throws SQLException {
        List<AuditLog> logs = new ArrayList<>();
        String sql = "SELECT al.*, u.email FROM audit_logs al LEFT JOIN users u ON al.user_id = u.user_id ORDER BY al.timestamp DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                AuditLog log = new AuditLog();
                log.setLogId(rs.getInt("log_id"));
                log.setUserId(rs.getInt("user_id"));
                log.setUserEmail(rs.getString("email"));
                log.setAction(rs.getString("action"));
                log.setDescription(rs.getString("description"));
                log.setIpAddress(rs.getString("ip_address"));
                log.setTimestamp(rs.getTimestamp("timestamp"));
                logs.add(log);
            }
        }
        return logs;
    }
}

