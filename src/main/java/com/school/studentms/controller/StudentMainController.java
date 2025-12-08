package com.school.studentms.controller;

import com.school.studentms.MainApp;
import com.school.studentms.service.DatabaseUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.collections.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.sql.*;

public class StudentMainController {

    private String studentId;
    private MainApp mainApp;

    // 背景面板
    @FXML private Pane backgroundPane;

    // 界面容器
    @FXML private AnchorPane contentPane;
    @FXML private VBox infoPane;
    @FXML private VBox coursesPane;
    @FXML private VBox gradesPane;
    @FXML private VBox passwordPane;

    // 导航按钮
    @FXML private Button btnMyInfo;
    @FXML private Button btnMyCourses;
    @FXML private Button btnMyGrades;
    @FXML private Button btnChangePassword;
    @FXML private Button btnLogout;

    // ========== 我的信息页面控件 ==========
    @FXML private ImageView genderImageView;
    @FXML private Label genderImageLabel;
    @FXML private TextField tfStudentId;
    @FXML private TextField tfName;
    @FXML private TextField tfGender;
    @FXML private TextField tfMajor;
    @FXML private TextField tfClass;
    @FXML private TextField tfPhone;
    @FXML private TextField tfEmail;
    @FXML private TextField tfEnrollmentDate;
    @FXML private TextArea taBio;
    @FXML private Button btnSaveBio;  // 新增的保存按钮

    // ========== 我的课程页面控件 ==========
    @FXML private TableView<Course> courseTable;
    @FXML private TableColumn<Course, String> colCourseCode;
    @FXML private TableColumn<Course, String> colCourseName;
    @FXML private TableColumn<Course, String> colCourseTeacher;
    @FXML private TableColumn<Course, Integer> colCourseCredit;
    @FXML private TableColumn<Course, String> colCourseTime;

    // 课程统计
    @FXML private Label lblCourseCount;
    @FXML private Label lblTotalCredits;
    @FXML private Label lblRequiredCount;
    @FXML private Label lblElectiveCount;

    // ========== 课程成绩页面控件 ==========
    @FXML private TableView<Grade> gradeTable;
    @FXML private TableColumn<Grade, String> colGradeCourseCode;
    @FXML private TableColumn<Grade, String> colGradeCourseName;
    @FXML private TableColumn<Grade, Double> colGradeScore;
    @FXML private TableColumn<Grade, Integer> colGradeCredit;
    @FXML private TableColumn<Grade, Double> colGradeGPA;
    @FXML private TableColumn<Grade, String> colGradeStatus;

    // 成绩统计
    @FXML private Label lblAverageScore;
    @FXML private Label lblAverageGPA;
    @FXML private Label lblHighestScore;
    @FXML private Label lblCompletedCredits;

    // 成绩分布
    @FXML private Label lblExcellentCount;
    @FXML private Label lblGoodCount;
    @FXML private Label lblMediumCount;
    @FXML private Label lblPassCount;
    @FXML private Label lblFailCount;

    // ========== 修改密码页面控件 ==========
    @FXML private PasswordField pfOld;
    @FXML private PasswordField pfNew;
    @FXML private PasswordField pfConfirm;

    // 数据列表
    private ObservableList<Course> courseData = FXCollections.observableArrayList();
    private ObservableList<Grade> gradeData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // 初始化我的课程表格
        initCourseTable();

        // 初始化成绩表格
        initGradeTable();

        // 设置导航按钮的事件处理
        setupNavigationButtons();

        // 设置个人简介保存按钮的事件
        if (btnSaveBio != null) {
            btnSaveBio.setOnAction(e -> handleSaveBio());
        }

        // 默认显示"我的信息"页面
        showInfoPane();
    }

    // 初始化课程表格
    private void initCourseTable() {
        colCourseCode.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCourseCode()));
        colCourseName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCourseName()));
        colCourseTeacher.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTeacher()));
        colCourseCredit.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCredit()).asObject());
        colCourseTime.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCourseTime()));

        courseTable.setItems(courseData);
    }

    // 初始化成绩表格
    private void initGradeTable() {
        colGradeCourseCode.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCourseCode()));
        colGradeCourseName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCourseName()));
        colGradeScore.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getScore()).asObject());
        colGradeCredit.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCredit()).asObject());
        colGradeGPA.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getGpa()).asObject());
        colGradeStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));

        gradeTable.setItems(gradeData);
    }

    // 设置导航按钮
    private void setupNavigationButtons() {
        btnMyInfo.setOnAction(e -> showInfoPane());
        btnMyCourses.setOnAction(e -> showCoursesPane());
        btnMyGrades.setOnAction(e -> showGradesPane());
        btnChangePassword.setOnAction(e -> showPasswordPane());
    }

    // 显示我的信息页面
    private void showInfoPane() {
        setActiveButton(btnMyInfo);
        setBackgroundImage("/images/Student-bg.png");

        infoPane.setVisible(true);
        infoPane.setManaged(true);
        coursesPane.setVisible(false);
        coursesPane.setManaged(false);
        gradesPane.setVisible(false);
        gradesPane.setManaged(false);
        passwordPane.setVisible(false);
        passwordPane.setManaged(false);
    }

    // 显示我的课程页面
    private void showCoursesPane() {
        setActiveButton(btnMyCourses);
        setBackgroundImage("/images/student-bg-course.png");

        infoPane.setVisible(false);
        infoPane.setManaged(false);
        coursesPane.setVisible(true);
        coursesPane.setManaged(true);
        gradesPane.setVisible(false);
        gradesPane.setManaged(false);
        passwordPane.setVisible(false);
        passwordPane.setManaged(false);

        // 加载课程数据
        loadStudentCourses();
    }

    // 显示课程成绩页面
    private void showGradesPane() {
        setActiveButton(btnMyGrades);
        setBackgroundImage("/images/student-bg-score.png");

        infoPane.setVisible(false);
        infoPane.setManaged(false);
        coursesPane.setVisible(false);
        coursesPane.setManaged(false);
        gradesPane.setVisible(true);
        gradesPane.setManaged(true);
        passwordPane.setVisible(false);
        passwordPane.setManaged(false);

        // 加载成绩数据
        loadStudentGrades();
    }

    // 显示修改密码页面
    private void showPasswordPane() {
        setActiveButton(btnChangePassword);
        setBackgroundImage("/images/student-bg-password.png");

        infoPane.setVisible(false);
        infoPane.setManaged(false);
        coursesPane.setVisible(false);
        coursesPane.setManaged(false);
        gradesPane.setVisible(false);
        gradesPane.setManaged(false);
        passwordPane.setVisible(true);
        passwordPane.setManaged(true);

        // 清空密码字段
        pfOld.setText("");
        pfNew.setText("");
        pfConfirm.setText("");
    }

    // 设置背景图片
    private void setBackgroundImage(String imagePath) {
        backgroundPane.setStyle(
                "-fx-background-image: url('" + imagePath + "');" +
                        "-fx-background-size: cover;" +
                        "-fx-background-position: center;" +
                        "-fx-background-repeat: no-repeat;"
        );
    }

    // 设置活动按钮样式
    private void setActiveButton(Button activeButton) {
        // 重置所有按钮样式
        btnMyInfo.setStyle(null);
        btnMyCourses.setStyle(null);
        btnMyGrades.setStyle(null);
        btnChangePassword.setStyle(null);

        // 为活动按钮添加特殊样式
        activeButton.setStyle("-fx-background-color: #CC88EE; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(221, 160, 238, 0.8), 25, 0.6, 0, 7);");
    }

    // 添加 setMainApp 方法
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    /** LoginController 调用此方法传递学生学号 */
    public void setStudentId(String id) {
        this.studentId = id;
        loadStudentInfo();
        // 初始时不加载课程和成绩数据，等用户点击按钮时再加载
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

                // 设置入学时间（如果有的话）
                Date enrollmentDate = rs.getDate("created_at");
                if (enrollmentDate != null) {
                    tfEnrollmentDate.setText(enrollmentDate.toString());
                }

                // ========== 加载个人简介 ==========
                // 首先检查是否有bio字段
                try {
                    String bio = rs.getString("bio");
                    if (bio != null) {
                        taBio.setText(bio);
                    } else {
                        // 如果没有bio字段，尝试检查是否有introduction字段
                        String introduction = rs.getString("introduction");
                        if (introduction != null) {
                            taBio.setText(introduction);
                        } else {
                            taBio.setText(""); // 两个字段都没有，设为空
                        }
                    }
                } catch (SQLException e) {
                    // bio字段可能不存在，尝试introduction字段
                    try {
                        String introduction = rs.getString("introduction");
                        if (introduction != null) {
                            taBio.setText(introduction);
                        } else {
                            taBio.setText("");
                        }
                    } catch (SQLException ex) {
                        taBio.setText("");
                    }
                }

                // 更新性别图片和标签
                updateGenderImage(rs.getString("gender"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("错误", "加载学生信息失败：" + e.getMessage());
        }
    }

    /** 更新性别图片 */
    private void updateGenderImage(String gender) {
        try {
            String imagePath;
            String studentName = tfName.getText().trim();

            if ("男".equals(gender)) {
                imagePath = "/images/male.png";
                genderImageLabel.setText(studentName.isEmpty() ? "男性学生" : studentName);
            } else if ("女".equals(gender)) {
                imagePath = "/images/female.png";
                genderImageLabel.setText(studentName.isEmpty() ? "女性学生" : studentName);
            } else {
                genderImageView.setImage(null);
                genderImageLabel.setText("性别未知");
                return;
            }

            // 加载并显示图片
            Image image = new Image(imagePath);
            genderImageView.setImage(image);
        } catch (Exception e) {
            e.printStackTrace();
            genderImageView.setImage(null);
            genderImageLabel.setText("图片加载失败");
        }
    }

    /** 保存个人简介 */
    @FXML
    public void handleSaveBio() {
        String bio = taBio.getText().trim();

        // 验证简介长度（可选，设为500字限制）
        if (bio.length() > 500) {
            showAlert("提示", "个人简介不能超过500字！当前字数：" + bio.length());
            return;
        }

        try (Connection conn = DatabaseUtil.getConnection()) {
            boolean success = false;

            // 首先检查是否有bio字段
            try {
                // 尝试更新bio字段
                String sql = "UPDATE students SET bio = ? WHERE student_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, bio);
                    ps.setString(2, studentId);
                    int rowsAffected = ps.executeUpdate();
                    success = rowsAffected > 0;
                }
            } catch (SQLException e) {
                // bio字段可能不存在，尝试创建bio字段
                try {
                    // 添加bio字段
                    String alterSql = "ALTER TABLE students ADD COLUMN bio TEXT";
                    try (Statement stmt = conn.createStatement()) {
                        stmt.execute(alterSql);
                        System.out.println("已成功添加bio字段到students表");

                        // 再次尝试更新
                        String updateSql = "UPDATE students SET bio = ? WHERE student_id = ?";
                        try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                            ps.setString(1, bio);
                            ps.setString(2, studentId);
                            int rowsAffected = ps.executeUpdate();
                            success = rowsAffected > 0;
                        }
                    }
                } catch (SQLException ex) {
                    // 如果添加字段失败，可能字段已存在，尝试使用introduction字段
                    try {
                        String sql = "UPDATE students SET introduction = ? WHERE student_id = ?";
                        try (PreparedStatement ps = conn.prepareStatement(sql)) {
                            ps.setString(1, bio);
                            ps.setString(2, studentId);
                            int rowsAffected = ps.executeUpdate();
                            success = rowsAffected > 0;
                        }
                    } catch (SQLException ex2) {
                        // 尝试创建introduction字段
                        try {
                            String alterSql = "ALTER TABLE students ADD COLUMN introduction TEXT";
                            try (Statement stmt = conn.createStatement()) {
                                stmt.execute(alterSql);
                                System.out.println("已成功添加introduction字段到students表");

                                String updateSql = "UPDATE students SET introduction = ? WHERE student_id = ?";
                                try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                                    ps.setString(1, bio);
                                    ps.setString(2, studentId);
                                    int rowsAffected = ps.executeUpdate();
                                    success = rowsAffected > 0;
                                }
                            }
                        } catch (SQLException ex3) {
                            showAlert("错误", "无法保存个人简介：数据库字段创建失败");
                            return;
                        }
                    }
                }
            }

            if (success) {
                showAlert("成功", "个人简介已成功保存！");
            } else {
                showAlert("提示", "保存失败，未找到该学生记录！");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("错误", "保存个人简介失败：" + e.getMessage());
        }
    }

    /** 加载课程数据 - 根据现有数据库结构调整 */
    private void loadStudentCourses() {
        courseData.clear();

        // 根据你的数据库结构，检查学生是否在课程表中
        String[] courseTables = {"course_CS101", "course_CS102", "course_MA101"};

        int courseCount = 0;
        int totalCredits = 0;
        int requiredCount = 0;
        int electiveCount = 0;

        try (Connection conn = DatabaseUtil.getConnection()) {
            for (String tableName : courseTables) {
                String courseCode = tableName.replace("course_", "");

                // 检查学生是否在该课程中
                String checkSql = "SELECT 1 FROM " + tableName + " WHERE student_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
                    ps.setString(1, studentId);
                    ResultSet rs = ps.executeQuery();

                    if (rs.next()) {
                        // 获取课程详细信息
                        String courseName = getCourseName(courseCode, conn);

                        // 获取教师信息（如果有的话）
                        String teacher = getCourseTeacher(courseCode, conn);

                        // 获取课程学分（默认为3）
                        int credit = getCourseCredit(courseCode, conn);

                        // 获取上课时间（默认为"待安排"）
                        String courseTime = getCourseTime(courseCode, conn);

                        Course course = new Course(courseCode, courseName, teacher, credit, courseTime);
                        courseData.add(course);

                        courseCount++;
                        totalCredits += credit;

                        // 假设学分大于等于3的是必修课，小于3的是选修课
                        if (credit >= 3) {
                            requiredCount++;
                        } else {
                            electiveCount++;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // 更新统计信息
            lblCourseCount.setText(String.valueOf(courseCount));
            lblTotalCredits.setText(String.valueOf(totalCredits));
            lblRequiredCount.setText(String.valueOf(requiredCount));
            lblElectiveCount.setText(String.valueOf(electiveCount));

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("错误", "加载课程数据失败：" + e.getMessage());
        }
    }

    /** 获取课程名称 */
    private String getCourseName(String courseCode, Connection conn) {
        try {
            String sql = "SELECT course_name FROM courses WHERE course_code = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, courseCode);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return rs.getString("course_name");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return courseCode; // 如果找不到，返回课程代码
    }

    /** 获取课程教师 - 简化版本，实际可能需要从其他表获取 */
    private String getCourseTeacher(String courseCode, Connection conn) {
        // 这里可以根据实际情况从数据库获取
        // 暂时返回默认值
        switch (courseCode) {
            case "CS101": return "张老师";
            case "CS102": return "李老师";
            case "MA101": return "王老师";
            default: return "待分配";
        }
    }

    /** 获取课程学分 - 简化版本 */
    private int getCourseCredit(String courseCode, Connection conn) {
        // 这里可以根据实际情况从数据库获取
        // 暂时返回默认值
        switch (courseCode) {
            case "CS101": return 3;
            case "CS102": return 4;
            case "MA101": return 4;
            default: return 3;
        }
    }

    /** 获取上课时间 - 简化版本 */
    private String getCourseTime(String courseCode, Connection conn) {
        // 这里可以根据实际情况从数据库获取
        // 暂时返回默认值
        switch (courseCode) {
            case "CS101": return "周一 1-2节";
            case "CS102": return "周三 3-4节";
            case "MA101": return "周二 5-6节";
            default: return "待安排";
        }
    }

    /** 加载成绩数据 - 根据现有数据库结构调整 */
    private void loadStudentGrades() {
        gradeData.clear();

        try (Connection conn = DatabaseUtil.getConnection()) {
            String[] courseTables = {"course_CS101", "course_CS102", "course_MA101"};

            int excellentCount = 0, goodCount = 0, mediumCount = 0, passCount = 0, failCount = 0;
            double totalScore = 0, totalGPA = 0;
            int completedCredits = 0;
            double highestScore = 0;
            int gradedCourses = 0;

            for (String tableName : courseTables) {
                String courseCode = tableName.replace("course_", "");

                String sql = "SELECT score FROM " + tableName + " WHERE student_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, studentId);
                    ResultSet rs = ps.executeQuery();

                    if (rs.next()) {
                        Double score = rs.getDouble("score");
                        boolean hasScore = !rs.wasNull();

                        // 获取课程信息
                        String courseName = getCourseName(courseCode, conn);
                        int credit = getCourseCredit(courseCode, conn);

                        if (hasScore && score != null) {
                            // 有成绩，计算绩点
                            double gpa = calculateGPA(score);
                            String status = "已评分";

                            Grade grade = new Grade(courseCode, courseName, score, credit, gpa, status);
                            gradeData.add(grade);

                            // 统计信息
                            gradedCourses++;
                            totalScore += score;
                            totalGPA += gpa;
                            completedCredits += credit;

                            if (score > highestScore) {
                                highestScore = score;
                            }

                            // 成绩分布统计
                            if (score >= 90) {
                                excellentCount++;
                            } else if (score >= 80) {
                                goodCount++;
                            } else if (score >= 70) {
                                mediumCount++;
                            } else if (score >= 60) {
                                passCount++;
                            } else {
                                failCount++;
                            }
                        } else {
                            // 没有成绩
                            Grade grade = new Grade(courseCode, courseName, null, credit, 0.0, "未评分");
                            gradeData.add(grade);
                        }
                    } else {
                        // 学生不在该课程中
                        String courseName = getCourseName(courseCode, conn);
                        int credit = getCourseCredit(courseCode, conn);
                        Grade grade = new Grade(courseCode, courseName, null, credit, 0.0, "未选课");
                        gradeData.add(grade);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // 更新统计信息
            if (gradedCourses > 0) {
                double averageScore = totalScore / gradedCourses;
                double averageGPA = totalGPA / gradedCourses;

                lblAverageScore.setText(String.format("%.2f", averageScore));
                lblAverageGPA.setText(String.format("%.2f", averageGPA));
                lblHighestScore.setText(String.format("%.1f", highestScore));
                lblCompletedCredits.setText(String.valueOf(completedCredits));
            } else {
                lblAverageScore.setText("0.00");
                lblAverageGPA.setText("0.00");
                lblHighestScore.setText("0");
                lblCompletedCredits.setText("0");
            }

            // 更新成绩分布
            lblExcellentCount.setText(excellentCount + "门");
            lblGoodCount.setText(goodCount + "门");
            lblMediumCount.setText(mediumCount + "门");
            lblPassCount.setText(passCount + "门");
            lblFailCount.setText(failCount + "门");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("错误", "加载成绩数据失败：" + e.getMessage());
        }
    }

    /** 计算绩点 */
    private double calculateGPA(double score) {
        if (score >= 90) return 4.0;
        if (score >= 85) return 3.7;
        if (score >= 82) return 3.3;
        if (score >= 78) return 3.0;
        if (score >= 75) return 2.7;
        if (score >= 72) return 2.3;
        if (score >= 68) return 2.0;
        if (score >= 64) return 1.5;
        if (score >= 60) return 1.0;
        return 0.0;
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
                    Stage currentStage = (Stage) btnLogout.getScene().getWindow();
                    currentStage.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert("错误", "退出登录失败：" + e.getMessage());
                }
            }
        });
    }

    @FXML
    public void handleChangePassword() {
        String oldP = pfOld.getText();
        String newP = pfNew.getText();
        String confirm = pfConfirm.getText();

        // 验证输入
        if (oldP.isEmpty() || newP.isEmpty() || confirm.isEmpty()) {
            showAlert("错误", "请填写所有密码字段");
            return;
        }

        if (!newP.equals(confirm)) {
            showAlert("错误", "两次新密码不一致！");
            return;
        }

        if (newP.length() < 6 || newP.length() > 20) {
            showAlert("错误", "密码长度必须在6-20位之间！");
            return;
        }

        try (Connection conn = DatabaseUtil.getConnection()) {
            // 验证旧密码
            String check = "SELECT password FROM students WHERE student_id = ? AND password = ?";
            PreparedStatement ps1 = conn.prepareStatement(check);
            ps1.setString(1, studentId);
            ps1.setString(2, oldP);
            ResultSet rs = ps1.executeQuery();

            if (!rs.next()) {
                showAlert("错误", "旧密码错误！");
                return;
            }

            // 更新密码
            String update = "UPDATE students SET password = ? WHERE student_id = ?";
            PreparedStatement ps2 = conn.prepareStatement(update);
            ps2.setString(1, newP);
            ps2.setString(2, studentId);
            ps2.executeUpdate();

            showAlert("成功", "密码修改成功！");

            // 清空密码字段
            pfOld.setText("");
            pfNew.setText("");
            pfConfirm.setText("");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("错误", "修改密码失败！");
        }
    }

    @FXML
    public void handleCancelPasswordChange() {
        showInfoPane();
    }

    /** 显示警告框 */
    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.show();
    }

    /** 课程数据模型 */
    public static class Course {
        private final String courseCode;
        private final String courseName;
        private final String teacher;
        private final int credit;
        private final String courseTime;

        public Course(String courseCode, String courseName, String teacher, int credit, String courseTime) {
            this.courseCode = courseCode;
            this.courseName = courseName;
            this.teacher = teacher;
            this.credit = credit;
            this.courseTime = courseTime;
        }

        public String getCourseCode() { return courseCode; }
        public String getCourseName() { return courseName; }
        public String getTeacher() { return teacher; }
        public int getCredit() { return credit; }
        public String getCourseTime() { return courseTime; }
    }

    /** 成绩数据模型 */
    public static class Grade {
        private final String courseCode;
        private final String courseName;
        private final Double score;
        private final int credit;
        private final double gpa;
        private final String status;

        public Grade(String courseCode, String courseName, Double score, int credit, double gpa, String status) {
            this.courseCode = courseCode;
            this.courseName = courseName;
            this.score = score;
            this.credit = credit;
            this.gpa = gpa;
            this.status = status;
        }

        public String getCourseCode() { return courseCode; }
        public String getCourseName() { return courseName; }
        public Double getScore() { return score == null ? 0.0 : score; }
        public int getCredit() { return credit; }
        public double getGpa() { return gpa; }
        public String getStatus() { return status; }
    }
}