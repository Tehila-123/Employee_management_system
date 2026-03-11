package com.ems.dao;

import com.ems.model.User;
import java.sql.SQLException;
import java.util.Optional;

public interface UserDAO {
    void createUser(User user) throws SQLException;
    Optional<User> getUserByEmail(String email) throws SQLException;
    void updateFailedAttempts(int userId, int attempts) throws SQLException;
    void lockAccount(int userId) throws SQLException;
    void resetFailedAttempts(int userId) throws SQLException;
    boolean existsByEmail(String email) throws SQLException;
}

