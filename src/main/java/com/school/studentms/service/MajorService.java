package com.school.studentms.service;

import com.school.studentms.model.Major;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MajorService {
    public List<Major> getAllMajors() {
        List<Major> majors = new ArrayList<>();
        String sql = "SELECT major_code, major_name FROM majors ORDER BY major_code";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Major major = new Major();
                major.setMajorCode(rs.getString("major_code"));
                major.setMajorName(rs.getString("major_name"));
                majors.add(major);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询专业列表失败: " + e.getMessage());
        }
        return majors;
    }

    public boolean addMajor(Major major) {
        String sql = "INSERT INTO majors (major_code, major_name) VALUES (?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, major.getMajorCode());
            pstmt.setString(2, major.getMajorName());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("新增专业失败: " + e.getMessage());
        }
    }

    public boolean updateMajor(Major major) {
        String sql = "UPDATE majors SET major_name = ? WHERE major_code = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, major.getMajorName());
            pstmt.setString(2, major.getMajorCode());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("更新专业失败: " + e.getMessage());
        }
    }

    public boolean deleteMajor(String majorCode) {
        String sql = "DELETE FROM majors WHERE major_code = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, majorCode);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("删除专业失败: " + e.getMessage());
        }
    }

    public boolean isMajorNameExists(String majorName) {
        String sql = "SELECT COUNT(*) FROM majors WHERE major_name = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, majorName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isMajorCodeExists(String majorCode) {
        String sql = "SELECT COUNT(*) FROM majors WHERE major_code = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, majorCode);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}