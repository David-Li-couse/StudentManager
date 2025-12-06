package com.school.studentms.controller;

import com.school.studentms.MainApp;
import com.school.studentms.service.DatabaseUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.*;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import java.sql.*;
import java.util.Timer;
import java.util.TimerTask;

import javafx.scene.Parent;
import javafx.scene.Scene;


public class StudentMainController {

    private String studentId; // 登录时由 LoginController 传入
    private MainApp mainApp;

    // 学生信息控件
    @FXML private TextField tfStudentId;
    @FXML private TextField tfName;
    @FXML private TextField tfGender;
    @FXML private TextField tfMajor;
    @FXML private TextField tfClass;
    @FXML private TextField tfPhone;
    @FXML private TextField tfEmail;

    // 课程表控件
    @FXML private TableView<CourseRecord> courseTable;
    @FXML private TableColumn<CourseRecord, String> colCourseName;
    @FXML private TableColumn<CourseRecord, String> colScore;

    // 修改密码控件
    @FXML private PasswordField pfOld;
    @FXML private PasswordField pfNew;
    @FXML private PasswordField pfConfirm;

    @FXML private Button btnLogout;  // 退出登录按钮

    @FXML
    public void initialize() {
        colCourseName.setCellValueFactory(data -> data.getValue().courseNameProperty());
        colScore.setCellValueFactory(data -> data.getValue().scoreProperty());
    }

    // 添加 setMainApp 方法
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    /** LoginController 调用此方法传递学生学号 */
    public void setStudentId(String id) {
        this.studentId = id;
        loadStudentInfo();
        loadStudentCourses();
    }

    /** 加载学生基本信息 */
    private void loadStudentInfo() {
        String sql = "SELECT * FROM students WHERE student_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, studentId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                tfStudentId.setText(rs.getString("student_id"));
                tfName.setText(rs.getString("name"));
                tfGender.setText(rs.getString("gender"));
                tfMajor.setText(rs.getString("major_name"));
                tfClass.setText(rs.getString("class_name"));
                tfPhone.setText(rs.getString("phone"));
                tfEmail.setText(rs.getString("email"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** 加载课程 */
    private void loadStudentCourses() {
        ObservableList<CourseRecord> list = FXCollections.observableArrayList();

        try (Connection conn = DatabaseUtil.getConnection()) {

            // 只查询 course_CS101, course_CS102, course_MA101 表
            String[] courseTables = {"course_CS101", "course_CS102", "course_MA101"};

            for (String tableName : courseTables) {
                String sql = "SELECT score FROM " + tableName + " WHERE student_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, studentId);
                    ResultSet rs = ps.executeQuery();

                    if (rs.next()) {
                        String scoreStr = (rs.getObject("score") == null)
                                ? "未评分"
                                : rs.getString("score");

                        // 课程名来自 courses 表
                        String courseCode = tableName.replace("course_", "");
                        String courseName = getCourseName(courseCode, conn);

                        list.add(new CourseRecord(courseName, scoreStr));
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        courseTable.setItems(list);
    }

    /** 从 courses 获取课程名称 */
    private String getCourseName(String courseCode, Connection conn) throws SQLException {
        String sql = "SELECT course_name FROM courses WHERE course_code = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, courseCode);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("course_name");
        }
        return courseCode; // fallback
    }

    @FXML
    public void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("确认退出");
        alert.setHeaderText("确定要退出登录吗？");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    System.out.println("学生退出登录，学号：" + studentId);

                    // 获取当前窗口
                    Stage currentStage = (Stage) btnLogout.getScene().getWindow();

                    // 关闭当前窗口（窗口的关闭事件会自动处理返回登录）
                    currentStage.close();

                    // 注意：不需要再调用 mainApp.showLoginWindow()
                    // 因为窗口的关闭事件已经在 MainApp 中设置了

                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert("错误", "退出登录失败：" + e.getMessage());
                }
            }
        });
    }

    /** 显示警告框 */
    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.show();
    }

    /** 课程记录内部类 */
    public static class CourseRecord {
        private final javafx.beans.property.SimpleStringProperty courseName;
        private final javafx.beans.property.SimpleStringProperty score;

        public CourseRecord(String name, String score) {
            this.courseName = new javafx.beans.property.SimpleStringProperty(name);
            this.score = new javafx.beans.property.SimpleStringProperty(score);
        }

        public javafx.beans.property.StringProperty courseNameProperty() { return courseName; }
        public javafx.beans.property.StringProperty scoreProperty() { return score; }
    }
    @FXML
    public void handleChangePassword() {
        String oldP = pfOld.getText();
        String newP = pfNew.getText();
        String confirm = pfConfirm.getText();

        if (newP.isEmpty() || confirm.isEmpty()) {
            showAlert("错误", "密码不能为空");
            return;
        }
        if (!newP.equals(confirm)) {
            showAlert("错误", "两次新密码不一致！");
            return;
        }

        try (Connection conn = DatabaseUtil.getConnection()) {

            // 验证旧密码，查询 students 表中的密码
            String check = "SELECT password FROM students WHERE student_id = ? AND password = ?";
            PreparedStatement ps1 = conn.prepareStatement(check);
            ps1.setString(1, studentId);
            ps1.setString(2, oldP);
            ResultSet rs = ps1.executeQuery();

            if (!rs.next()) {
                showAlert("错误", "旧密码错误！");
                return;
            }

            // 更新密码，修改 students 表中的密码字段
            String update = "UPDATE students SET password = ? WHERE student_id = ?";
            PreparedStatement ps2 = conn.prepareStatement(update);
            ps2.setString(1, newP);
            ps2.setString(2, studentId);
            ps2.executeUpdate();

            showAlert("成功", "密码修改成功！");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("错误", "修改密码失败！");
        }
    }
}
