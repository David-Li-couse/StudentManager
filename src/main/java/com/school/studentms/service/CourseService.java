package com.school.studentms.service;

import com.school.studentms.model.Course;
import com.school.studentms.model.Student;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseService {
    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT course_code, course_name FROM courses ORDER BY course_code";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Course course = new Course();
                course.setCourseCode(rs.getString("course_code"));
                course.setCourseName(rs.getString("course_name"));
                courses.add(course);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询课程列表失败: " + e.getMessage());
        }
        return courses;
    }

    public boolean addCourse(Course course) {
        String sql = "INSERT INTO courses (course_code, course_name) VALUES (?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, course.getCourseCode());
            pstmt.setString(2, course.getCourseName());
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                // 创建课程对应的表
                createCourseTable(course.getCourseCode());
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("新增课程失败: " + e.getMessage());
        }
    }

    private void createCourseTable(String courseCode) {
        // 创建课程表的SQL，表名使用课程代码，但需要确保安全（避免SQL注入）
        String tableName = "course_" + courseCode.replaceAll("[^a-zA-Z0-9_]", "_");
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "student_id VARCHAR(20) NOT NULL, " +
                "student_name VARCHAR(50) NOT NULL, " +
                "gender ENUM('男', '女') NOT NULL, " +
                "major_name VARCHAR(100) NOT NULL, " +
                "score DECIMAL(5,2) DEFAULT NULL, " +
                "FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("创建课程表失败: " + e.getMessage());
        }
    }

    public boolean updateCourse(Course course) {
        String sql = "UPDATE courses SET course_name = ? WHERE course_code = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, course.getCourseName());
            pstmt.setString(2, course.getCourseCode());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("更新课程失败: " + e.getMessage());
        }
    }

    public boolean deleteCourse(String courseCode) {
        // 先删除课程对应的表
        dropCourseTable(courseCode);

        String sql = "DELETE FROM courses WHERE course_code = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, courseCode);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("删除课程失败: " + e.getMessage());
        }
    }

    private void dropCourseTable(String courseCode) {
        String tableName = "course_" + courseCode.replaceAll("[^a-zA-Z0-9_]", "_");
        String sql = "DROP TABLE IF EXISTS " + tableName;

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("删除课程表失败: " + e.getMessage());
        }
    }

    public boolean isCourseNameExists(String courseName) {
        String sql = "SELECT COUNT(*) FROM courses WHERE course_name = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, courseName);
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

    public boolean isCourseCodeExists(String courseCode) {
        String sql = "SELECT COUNT(*) FROM courses WHERE course_code = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, courseCode);
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

    private boolean isStudentInCourse(String courseCode, String studentId) {
        String tableName = "course_" + courseCode.replaceAll("[^a-zA-Z0-9_]", "_");
        String sql = "SELECT COUNT(*) FROM " + tableName + " WHERE student_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            // 如果表不存在，返回false
            return false;
        }
        return false;
    }

    public boolean addStudentToCourse(String courseCode, String studentId) {
        // 先获取学生信息
        StudentService studentService = new StudentService();
        List<Student> allStudents = studentService.getAllStudents();
        Student student = allStudents.stream()
                .filter(s -> s.getStudentId().equals(studentId))
                .findFirst()
                .orElse(null);

        if (student == null) {
            return false;
        }

        String tableName = "course_" + courseCode.replaceAll("[^a-zA-Z0-9_]", "_");
        // 检查学生是否已在课程中
        if (isStudentInCourse(courseCode, studentId)) {
            throw new RuntimeException("该学生已经在此课程中，不能重复分配");
        }
        String sql = "INSERT INTO " + tableName + " (student_id, student_name, gender, major_name) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, student.getStudentId());
            pstmt.setString(2, student.getName());
            pstmt.setString(3, student.getGender());
            pstmt.setString(4, student.getMajorName());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("添加学生到课程失败: " + e.getMessage());
        }
    }

    public boolean updateStudentScore(String courseCode, String studentId, double score) {
        String tableName = "course_" + courseCode.replaceAll("[^a-zA-Z0-9_]", "_");
        String sql = "UPDATE " + tableName + " SET score = ? WHERE student_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, score);
            pstmt.setString(2, studentId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("更新学生成绩失败: " + e.getMessage());
        }
    }

    public List<Course> searchCourses(String keyword) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT course_code, course_name FROM courses " +
                "WHERE course_code LIKE ? OR course_name LIKE ? ORDER BY course_code";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String likeKeyword = "%" + keyword + "%";
            pstmt.setString(1, likeKeyword);
            pstmt.setString(2, likeKeyword);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Course course = new Course();
                    course.setCourseCode(rs.getString("course_code"));
                    course.setCourseName(rs.getString("course_name"));
                    courses.add(course);  // 将课程对象添加到列表
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("搜索课程失败: " + e.getMessage());
        }
        return courses;  // 返回课程列表
    }

    public Double getStudentScore(String courseCode, String studentId) {
        String tableName = "course_" + courseCode.replaceAll("[^a-zA-Z0-9_]", "_");
        String sql = "SELECT score FROM " + tableName + " WHERE student_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("score");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 获取课程学生的方法以包含成绩
    public List<Student> getStudentsInCourse(String courseCode) {
        List<Student> students = new ArrayList<>();
        String tableName = "course_" + courseCode.replaceAll("[^a-zA-Z0-9_]", "_");

        String sql = "SELECT s.id, s.student_id, s.name, s.gender, s.major_name, s.class_name, c.score " +
                "FROM " + tableName + " c " +
                "JOIN students s ON c.student_id = s.student_id " +
                "ORDER BY s.student_id";

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

                // 处理成绩字段（可能为NULL）
                double score = rs.getDouble("score");
                if (!rs.wasNull()) {
                    student.setScore(score);
                } else {
                    student.setScore(null);
                }

                students.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询课程学生失败: " + e.getMessage());
        }
        return students;
    }

    public boolean removeStudentFromCourse(String courseCode, String studentId) {
        // 检查参数是否有效
        if (courseCode == null || courseCode.trim().isEmpty() ||
                studentId == null || studentId.trim().isEmpty()) {
            throw new IllegalArgumentException("课程代码或学号不能为空");
        }

        String tableName = "course_" + courseCode.replaceAll("[^a-zA-Z0-9_]", "_");
        String sql = "DELETE FROM " + tableName + " WHERE student_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("从课程中移除学生失败: " + e.getMessage());
        }
    }
}