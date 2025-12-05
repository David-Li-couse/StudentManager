package com.school.studentms.service;

import java.sql.*;

public class LoginService {

    public boolean adminLogin(String username, String password) {
        return "admin".equals(username) && "123456".equals(password);
    }

    public boolean studentLogin(String username, String password) {
        String sql = "SELECT password FROM students WHERE student_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("password");
                    return storedPassword.equals(password);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("学生登录失败: " + e.getMessage());
        }
        return false;
    }

}
