package com.school.studentms.service;

import com.school.studentms.model.Student;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentService {
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT id, student_id, name, gender, major_name, class_name, phone, email FROM students ORDER BY id";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Student student = new Student();
                student.setId(rs.getInt("id"));
                student.setStudentId(rs.getString("student_id"));
                student.setName(rs.getString("name"));
                student.setGender(rs.getString("gender"));
                student.setMajorName(rs.getString("major_name"));
                student.setClassName(rs.getString("class_name"));
                student.setPhone(rs.getString("phone"));
                student.setEmail(rs.getString("email"));
                students.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询学生列表失败: " + e.getMessage());
        }
        return students;
    }

    public Student getStudentById(int id) {
        String sql = "SELECT id, student_id, name, gender, major_name, class_name, phone, email FROM students WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Student student = new Student();
                    student.setId(rs.getInt("id"));
                    student.setStudentId(rs.getString("student_id"));
                    student.setName(rs.getString("name"));
                    student.setGender(rs.getString("gender"));
                    student.setMajorName(rs.getString("major_name"));
                    student.setClassName(rs.getString("class_name"));
                    student.setPhone(rs.getString("phone"));
                    student.setEmail(rs.getString("email"));
                    return student;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询学生信息失败: " + e.getMessage());
        }
        return null;
    }

    public int addStudent(Student student) {
        String sql = "INSERT INTO students (student_id, name, gender, major_name, class_name, phone, email) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, student.getStudentId());
            pstmt.setString(2, student.getName());
            pstmt.setString(3, student.getGender());
            pstmt.setString(4, student.getMajorName());
            pstmt.setString(5, student.getClassName());
            pstmt.setString(6, student.getPhone());
            pstmt.setString(7, student.getEmail());
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("创建学生失败，没有行受影响。");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("创建学生失败，无法获取ID。");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("新增学生失败: " + e.getMessage());
        }
    }

    public void updateStudent(Student student) {
        String sql = "UPDATE students SET student_id = ?, name = ?, gender = ?, major_name = ?, class_name = ?, phone = ?, email = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, student.getStudentId());
            pstmt.setString(2, student.getName());
            pstmt.setString(3, student.getGender());
            pstmt.setString(4, student.getMajorName());
            pstmt.setString(5, student.getClassName());
            pstmt.setString(6, student.getPhone());
            pstmt.setString(7, student.getEmail());
            pstmt.setInt(8, student.getId());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("更新学生失败，没有找到对应ID的记录。");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("更新学生失败: " + e.getMessage());
        }
    }

    public void deleteStudent(int id) {
        String sql = "DELETE FROM students WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("删除学生失败，没有找到对应ID的记录。");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("删除学生失败: " + e.getMessage());
        }
    }

    public List<Student> searchStudents(String keyword) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT id, student_id, name, gender, major_name, class_name, phone, email FROM students " +
                "WHERE student_id LIKE ? OR name LIKE ? OR major_name LIKE ? OR class_name LIKE ? ORDER BY id";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String likeKeyword = "%" + keyword + "%";
            pstmt.setString(1, likeKeyword);
            pstmt.setString(2, likeKeyword);
            pstmt.setString(3, likeKeyword);
            pstmt.setString(4, likeKeyword);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Student student = new Student();
                    student.setId(rs.getInt("id"));
                    student.setStudentId(rs.getString("student_id"));
                    student.setName(rs.getString("name"));
                    student.setGender(rs.getString("gender"));
                    student.setMajorName(rs.getString("major_name"));
                    student.setClassName(rs.getString("class_name"));
                    student.setPhone(rs.getString("phone"));
                    student.setEmail(rs.getString("email"));
                    students.add(student);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("搜索学生失败: " + e.getMessage());
        }
        return students;
    }

    public void resetPassword(int id) {
        String sql = "UPDATE students SET password = 123456 WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("重置密码失败，没有找到对应ID的记录。");
            }
        }catch (SQLException e){
            e.printStackTrace();
            throw new RuntimeException("重置密码失败: " + e.getMessage());
        }
    }
}