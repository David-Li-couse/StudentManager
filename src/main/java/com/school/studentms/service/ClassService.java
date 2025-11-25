package com.school.studentms.service;

import com.school.studentms.model.Clazz;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClassService {
    public List<Clazz> getAllClasses() {
        List<Clazz> classes = new ArrayList<>();
        String sql = "SELECT class_code, class_name, major_code FROM classes ORDER BY class_code";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Clazz clazz = new Clazz();
                clazz.setClassCode(rs.getInt("class_code"));
                clazz.setClassName(rs.getString("class_name"));
                clazz.setMajorCode(rs.getString("major_code"));
                classes.add(clazz);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询班级列表失败: " + e.getMessage());
        }
        return classes;
    }

    public List<Clazz> getClassesByMajor(String majorCode) {
        List<Clazz> classes = new ArrayList<>();
        String sql = "SELECT class_code, class_name, major_code FROM classes WHERE major_code = ? ORDER BY class_code";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, majorCode);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Clazz clazz = new Clazz();
                    clazz.setClassCode(rs.getInt("class_code"));
                    clazz.setClassName(rs.getString("class_name"));
                    clazz.setMajorCode(rs.getString("major_code"));
                    classes.add(clazz);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询班级列表失败: " + e.getMessage());
        }
        return classes;
    }

    public boolean addClass(Clazz clazz) {
        String sql = "INSERT INTO classes (class_name, major_code) VALUES (?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, clazz.getClassName());
            pstmt.setString(2, clazz.getMajorCode());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("新增班级失败: " + e.getMessage());
        }
    }

    public boolean updateClass(Clazz clazz) {
        String sql = "UPDATE classes SET class_name = ?, major_code = ? WHERE class_code = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, clazz.getClassName());
            pstmt.setString(2, clazz.getMajorCode());
            pstmt.setInt(3, clazz.getClassCode());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("更新班级失败: " + e.getMessage());
        }
    }

    public boolean deleteClass(int classCode) {
        String sql = "DELETE FROM classes WHERE class_code = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, classCode);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("删除班级失败: " + e.getMessage());
        }
    }

    public boolean isClassNameExists(String className) {
        String sql = "SELECT COUNT(*) FROM classes WHERE class_name = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, className);
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