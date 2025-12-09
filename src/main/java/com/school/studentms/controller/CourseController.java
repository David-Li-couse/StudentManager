package com.school.studentms.controller;

import com.school.studentms.MainApp;
import com.school.studentms.model.Course;
import com.school.studentms.model.Student;
import com.school.studentms.service.CourseService;
import com.school.studentms.service.StudentService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import com.school.studentms.utils.AlertUtil;

import java.util.List;

public class CourseController {
    @FXML private AnchorPane courseView;

    // 课程管理相关控件
    @FXML private TableView<Course> courseTable;
    @FXML private TableColumn<Course, String> colCourseCode;
    @FXML private TableColumn<Course, String> colCourseName;
    @FXML private TextField courseCodeField;
    @FXML private TextField courseNameField;
    @FXML private Button addCourseButton;
    @FXML private Button updateCourseButton;
    @FXML private Button deleteCourseButton;
    @FXML private TextField courseSearchField;

    // 学生分配相关控件
    @FXML private TableView<Student> allStudentsTable;
    @FXML private TableColumn<Student, String> colAllStudentId;
    @FXML private TableColumn<Student, String> colAllStudentName;
    @FXML private TableColumn<Student, String> colAllStudentGender;
    @FXML private TableColumn<Student, String> colAllStudentMajor;
    @FXML private Button assignStudentButton;

    // 课程学生管理相关控件
    @FXML private TableView<Student> courseStudentsTable;
    @FXML private TableColumn<Student, String> colCourseStudentId;
    @FXML private TableColumn<Student, String> colCourseStudentName;
    @FXML private TableColumn<Student, String> colCourseStudentGender;
    @FXML private TableColumn<Student, String> colCourseStudentMajor;
    @FXML private TableColumn<Student, Double> colCourseStudentScore;
    @FXML private TextField scoreField;
    @FXML private Button updateScoreButton;
    @FXML private Button removeStudentButton;

    private final CourseService courseService = new CourseService();
    private final StudentService studentService = new StudentService();
    private ObservableList<Course> courseData = FXCollections.observableArrayList();
    private ObservableList<Student> allStudentsData = FXCollections.observableArrayList();
    private ObservableList<Student> courseStudentsData = FXCollections.observableArrayList();
    private MainApp mainApp;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void handleBackToStudent() {
        // 关闭当前窗口
        Stage stage = (Stage) courseView.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void initialize() {
        // 初始化课程表格
        colCourseCode.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        colCourseName.setCellValueFactory(new PropertyValueFactory<>("courseName"));

        // 初始化所有学生表格
        colAllStudentId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colAllStudentName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colAllStudentGender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        colAllStudentMajor.setCellValueFactory(new PropertyValueFactory<>("majorName"));

        // 初始化课程学生表格
        colCourseStudentId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colCourseStudentName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCourseStudentGender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        colCourseStudentMajor.setCellValueFactory(new PropertyValueFactory<>("majorName"));
        colCourseStudentScore.setCellValueFactory(new PropertyValueFactory<>("score"));

        // 加载数据
        loadCourseData();
        loadAllStudentsData();

        // 设置表格选择监听器
        courseTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    showCourseDetails(newValue);
                    if (newValue != null) {
                        loadCourseStudentsData(newValue.getCourseCode());
                    } else {
                        courseStudentsData.clear();
                    }
                });

        courseStudentsTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showStudentScoreDetails(newValue));
    }

    private void loadCourseData() {
        courseData.clear();
        courseData.addAll(courseService.getAllCourses());
        courseTable.setItems(courseData);
    }

    private void loadAllStudentsData() {
        allStudentsData.clear();
        allStudentsData.addAll(studentService.getAllStudents());
        allStudentsTable.setItems(allStudentsData);
    }

    private void loadCourseStudentsData(String courseCode) {
        courseStudentsData.clear();
        List<Student> students = courseService.getStudentsInCourse(courseCode);
        courseStudentsData.addAll(students);
        courseStudentsTable.setItems(courseStudentsData);
    }

    private void showCourseDetails(Course course) {
        if (course != null) {
            courseCodeField.setText(course.getCourseCode());
            courseNameField.setText(course.getCourseName());
        } else {
            clearCourseDetails();
        }
    }

    private void showStudentScoreDetails(Student student) {
        if (student != null) {
            // 这里可以显示学生成绩，但需要扩展Student模型来包含成绩
            // 暂时留空，可以在表格中直接显示成绩
        } else {
            scoreField.setText("");
        }
    }

    private void clearCourseDetails() {
        courseCodeField.setText("");
        courseNameField.setText("");
    }

    @FXML
    private void handleRefreshCourses() {
        loadCourseData();
        AlertUtil.showInfoAlert("刷新成功", "课程数据已刷新！");
    }

    @FXML
    private void handleRefreshAllStudents() {
        loadAllStudentsData();
        AlertUtil.showInfoAlert("刷新成功", "学生数据已刷新！");
    }

    @FXML
    private void handleRefreshCourseStudents() {
        Course selectedCourse = courseTable.getSelectionModel().getSelectedItem();
        if (selectedCourse != null) {
            loadCourseStudentsData(selectedCourse.getCourseCode());
            AlertUtil.showInfoAlert("刷新成功", "课程学生数据已刷新！");
        } else {
            AlertUtil.showInfoAlert("提示", "请先选择一个课程！");
        }
    }

    @FXML
    private void handleAddCourse() {
        String courseCode = courseCodeField.getText().trim();
        String courseName = courseNameField.getText().trim();

        if (courseCode.isEmpty() || courseName.isEmpty()) {
            AlertUtil.showErrorAlert("输入错误", "请填写所有课程字段！");
            return;
        }

        if (courseService.isCourseCodeExists(courseCode)) {
            AlertUtil.showErrorAlert("输入错误", "课程代码已存在！");
            return;
        }

        if (courseService.isCourseNameExists(courseName)) {
            AlertUtil.showErrorAlert("输入错误", "课程名称已存在！");
            return;
        }

        try {
            Course course = new Course(courseCode, courseName);
            boolean success = courseService.addCourse(course);
            if (success) {
                courseData.add(course);
                clearCourseDetails();
                AlertUtil.showInfoAlert("成功", "课程添加成功！");
            }
        } catch (Exception e) {
            AlertUtil.showErrorAlert("错误", "添加课程失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdateCourse() {
        Course selectedCourse = courseTable.getSelectionModel().getSelectedItem();
        if (selectedCourse == null) {
            AlertUtil.showErrorAlert("选择错误", "请先选择一个课程进行修改！");
            return;
        }

        String courseCode = courseCodeField.getText().trim();
        String courseName = courseNameField.getText().trim();

        if (courseCode.isEmpty() || courseName.isEmpty()) {
            AlertUtil.showErrorAlert("输入错误", "请填写所有课程字段！");
            return;
        }

        // 检查名称是否与其他课程重复
        if (!selectedCourse.getCourseName().equals(courseName) &&
                courseService.isCourseNameExists(courseName)) {
            AlertUtil.showErrorAlert("输入错误", "课程名称已存在！");
            return;
        }

        try {
            selectedCourse.setCourseCode(courseCode);
            selectedCourse.setCourseName(courseName);
            boolean success = courseService.updateCourse(selectedCourse);
            if (success) {
                courseTable.refresh();
                AlertUtil.showInfoAlert("成功", "课程信息更新成功！");
            }
        } catch (Exception e) {
            AlertUtil.showErrorAlert("错误", "更新课程失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteCourse() {
        Course selectedCourse = courseTable.getSelectionModel().getSelectedItem();
        if (selectedCourse == null) {
            AlertUtil.showErrorAlert("选择错误", "请先选择一个课程进行删除！");
            return;
        }

        // 使用AlertUtil的确认对话框
        boolean confirmed = AlertUtil.showConfirmDialog(
                "确认删除",
                "你确定要删除这个课程吗？\n课程名称: " + selectedCourse.getCourseName()
        );

        if (confirmed) {
            try {
                boolean success = courseService.deleteCourse(selectedCourse.getCourseCode());
                if (success) {
                    courseData.remove(selectedCourse);
                    clearCourseDetails();
                    courseStudentsData.clear();
                    AlertUtil.showInfoAlert("成功", "课程删除成功！");
                } else {
                    AlertUtil.showErrorAlert("错误", "课程删除失败！");
                }
            } catch (Exception e) {
                AlertUtil.showErrorAlert("错误", "删除课程失败：" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleCourseSearch() {
        String keyword = courseSearchField.getText().trim();

        if (keyword.isEmpty()) {
            loadCourseData();
            return;
        }

        try {
            // 调用服务层的搜索方法，直接从数据库搜索
            List<Course> searchResults = courseService.searchCourses(keyword);

            // 将结果转换为 ObservableList 并设置到表格
            ObservableList<Course> filteredData = FXCollections.observableArrayList();
            filteredData.addAll(searchResults);
            courseTable.setItems(filteredData);

            // 显示搜索到的记录数
            if (searchResults.isEmpty()) {
                AlertUtil.showInfoAlert("搜索结果", "未找到匹配的课程");
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.showErrorAlert("搜索错误", "搜索课程时发生错误：" + e.getMessage());

            // 如果搜索失败，回退到全量加载和内存过滤
            loadCourseData();
            ObservableList<Course> filteredData = FXCollections.observableArrayList();
            for (Course course : courseData) {
                if (course.getCourseCode().toLowerCase().contains(keyword.toLowerCase()) ||
                        course.getCourseName().toLowerCase().contains(keyword.toLowerCase())) {
                    filteredData.add(course);
                }
            }
            courseTable.setItems(filteredData);
        }
    }

    @FXML
    private void clearCourseSearch() {
        courseSearchField.setText("");
        loadCourseData();
    }

    // 学生分配操作方法
    @FXML
    private void handleAssignStudent() {
        Course selectedCourse = courseTable.getSelectionModel().getSelectedItem();
        Student selectedStudent = allStudentsTable.getSelectionModel().getSelectedItem();

        if (selectedCourse == null) {
            AlertUtil.showErrorAlert("选择错误", "请先选择一个课程！");
            return;
        }

        if (selectedStudent == null) {
            AlertUtil.showErrorAlert("选择错误", "请先选择一个学生！");
            return;
        }

        try {
            boolean success = courseService.addStudentToCourse(
                    selectedCourse.getCourseCode(),
                    selectedStudent.getStudentId()
            );

            if (success) {
                loadCourseStudentsData(selectedCourse.getCourseCode());
                AlertUtil.showInfoAlert("成功", "学生分配成功！");
            }
        } catch (Exception e) {
            AlertUtil.showErrorAlert("错误", "分配学生失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdateScore() {
        Course selectedCourse = courseTable.getSelectionModel().getSelectedItem();
        Student selectedStudent = courseStudentsTable.getSelectionModel().getSelectedItem();

        if (selectedCourse == null) {
            AlertUtil.showErrorAlert("选择错误", "请先选择一个课程！");
            return;
        }

        if (selectedStudent == null) {
            AlertUtil.showErrorAlert("选择错误", "请先选择一个学生！");
            return;
        }

        String scoreText = scoreField.getText().trim();
        if (scoreText.isEmpty()) {
            AlertUtil.showErrorAlert("输入错误", "请输入成绩！");
            return;
        }

        try {
            double score = Double.parseDouble(scoreText);
            if (score < 0 || score > 100) {
                AlertUtil.showErrorAlert("输入错误", "成绩必须在0-100之间！");
                return;
            }

            boolean success = courseService.updateStudentScore(
                    selectedCourse.getCourseCode(),
                    selectedStudent.getStudentId(),
                    score
            );

            if (success) {
                loadCourseStudentsData(selectedCourse.getCourseCode());
                scoreField.setText("");
                AlertUtil.showInfoAlert("成功", "成绩更新成功！");
            }
        } catch (NumberFormatException e) {
            AlertUtil.showErrorAlert("输入错误", "成绩必须是数字！");
        } catch (Exception e) {
            AlertUtil.showErrorAlert("错误", "更新成绩失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRemoveStudent() {
        Course selectedCourse = courseTable.getSelectionModel().getSelectedItem();
        Student selectedStudent = courseStudentsTable.getSelectionModel().getSelectedItem();

        if (selectedCourse == null) {
            AlertUtil.showErrorAlert("选择错误", "请先选择一个课程！");
            return;
        }

        if (selectedStudent == null) {
            AlertUtil.showErrorAlert("选择错误", "请先选择一个学生！");
            return;
        }

        // 使用AlertUtil的确认对话框
        boolean confirmed = AlertUtil.showConfirmDialog(
                "确认移除",
                "你确定要从课程中移除这个学生吗？\n学生姓名: " + selectedStudent.getName()
        );

        if (confirmed) {
            try {
                boolean success = courseService.removeStudentFromCourse(
                        selectedCourse.getCourseCode(),
                        selectedStudent.getStudentId()
                );

                if (success) {
                    // 刷新课程学生列表
                    loadCourseStudentsData(selectedCourse.getCourseCode());
                    AlertUtil.showInfoAlert("成功", "学生移除成功！");
                }

            } catch (Exception e) {
                AlertUtil.showErrorAlert("错误", "移除学生失败：" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}