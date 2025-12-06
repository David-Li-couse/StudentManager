package com.school.studentms.service;

import com.school.studentms.model.Student;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginService {

    /**
     * 登录方法：
     * 返回：
     *   "admin"   —— 管理员登录成功
     *   "student" —— 学生登录成功
     *   null      —— 登录失败
     */
    public String login(String username, String password) {

        // --------------- 1. 判断管理员登录 -------------------
        String adminSql = "SELECT * FROM admins WHERE username=? AND password=?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(adminSql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return "admin";     // 管理员登录成功
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // --------------- 2. 判断学生登录 ---------------------
        String studentSql = "SELECT * FROM students WHERE student_id=? AND password=?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(studentSql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return "student";   // 学生登录成功
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null; // 登录失败
    }
}
