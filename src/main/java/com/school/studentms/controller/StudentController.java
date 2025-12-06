package com.school.studentms.controller;

import com.school.studentms.MainApp;
import com.school.studentms.model.Student;
import com.school.studentms.model.Major;
import com.school.studentms.model.Clazz;
import com.school.studentms.service.StudentService;
import com.school.studentms.service.MajorService;
import com.school.studentms.service.ClassService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import com.school.studentms.utils.AlertUtil;
import javafx.stage.Stage;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

public class StudentController {
    @FXML private AnchorPane studentView;
    @FXML private TableView<Student> studentTable;
    @FXML private TableColumn<Student, Integer> colId;
    @FXML private TableColumn<Student, String> colStudentId;
    @FXML private TableColumn<Student, String> colName;
    @FXML private TableColumn<Student, String> colGender;
    @FXML private TableColumn<Student, String> colMajor;
    @FXML private TableColumn<Student, String> colClass;
    @FXML private TextField studentIdField;
    @FXML private TextField nameField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private ComboBox<String> genderComboBox;
    @FXML private ComboBox<String> majorComboBox;
    @FXML private ComboBox<String> classComboBox;
    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private TextField searchField;
    @FXML private Button resetButton; // 新增的按钮
    @FXML private ImageView genderImageView; // 新增的ImageView
    @FXML private Label genderImageLabel; // 新增的Label
    @FXML private Button logoutButton; // 退出登录按钮
    @FXML
    private DatePicker enrollmentDatePicker;

    @FXML
    private DatePicker birthDatePicker;

    @FXML
    private TextField addressField;

    private final StudentService studentService = new StudentService();
    private final MajorService majorService = new MajorService();
    private final ClassService classService = new ClassService();
    private ObservableList<Student> studentData = FXCollections.observableArrayList();
    private ObservableList<String> majorData = FXCollections.observableArrayList();
    private ObservableList<String> classData = FXCollections.observableArrayList();
    private MainApp mainApp;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void handleMajorClassManagement() {
        if (mainApp != null) {
            mainApp.showMajorClassManagement();
        }
    }

    @FXML
    private void handleCourseManagement() {
        if (mainApp != null) {
            mainApp.showCourseManagement();
        }
    }

    @FXML
    private void initialize() {
        // 初始化表格列
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colStudentId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colGender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        colMajor.setCellValueFactory(new PropertyValueFactory<>("majorName"));
        colClass.setCellValueFactory(new PropertyValueFactory<>("className"));

        // 初始化下拉框
        genderComboBox.getItems().addAll("男", "女");
        loadMajorData();
        loadStudentData();

        // 初始化性别图片为默认状态
        updateGenderImage(null);

        // 专业选择监听器
        majorComboBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        loadClassDataByMajor(newValue);
                    } else {
                        classComboBox.getItems().clear();
                    }
                });

        // 表格选择监听器
        studentTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showStudentDetails(newValue));
    }

    private void loadMajorData() {
        majorData.clear();
        List<Major> majors = majorService.getAllMajors();
        List<String> majorNames = majors.stream()
                .map(Major::getMajorName)
                .collect(Collectors.toList());
        majorData.addAll(majorNames);
        majorComboBox.setItems(majorData);
    }

    private void loadClassDataByMajor(String majorName) {
        classData.clear();
        // 获取专业代码
        List<Major> majors = majorService.getAllMajors();
        String majorCode = majors.stream()
                .filter(m -> m.getMajorName().equals(majorName))
                .map(Major::getMajorCode)
                .findFirst()
                .orElse(null);

        if (majorCode != null) {
            List<Clazz> classes = classService.getClassesByMajor(majorCode);
            List<String> classNames = classes.stream()
                    .map(Clazz::getClassName)
                    .collect(Collectors.toList());
            classData.addAll(classNames);
        }
        classComboBox.setItems(classData);
    }

    private void loadStudentData() {
        studentData.clear();
        studentData.addAll(studentService.getAllStudents());
        studentTable.setItems(studentData);
    }

    private void showStudentDetails(Student student) {
        if (student != null) {
            studentIdField.setText(student.getStudentId());
            nameField.setText(student.getName());
            genderComboBox.setValue(student.getGender());
            majorComboBox.setValue(student.getMajorName());

            // 加载对应专业的班级
            if (student.getMajorName() != null) {
                loadClassDataByMajor(student.getMajorName());
                classComboBox.setValue(student.getClassName());
            }

            phoneField.setText(student.getPhone());
            emailField.setText(student.getEmail());

            // 更新性别图片
            updateGenderImage(student.getGender());
        } else {
            clearStudentDetails();
        }
    }

    private void clearStudentDetails() {
        studentIdField.setText("");
        nameField.setText("");
        genderComboBox.setValue(null);
        majorComboBox.setValue(null);
        classComboBox.setValue(null);
        classComboBox.getItems().clear();
        phoneField.setText("");
        emailField.setText("");

        // 重置性别图片
        updateGenderImage(null);
    }

    @FXML
    private void handleGenderChange() {
        // 当性别下拉框改变时更新图片
        String gender = genderComboBox.getValue();
        updateGenderImage(gender);
    }

    private void updateGenderImage(String gender) {
        try {
            if (gender == null || gender.trim().isEmpty()) {
                // 未选择性别或清空时
                genderImageView.setImage(null);
                genderImageLabel.setText("未选择学生");
                return;
            }

            String imagePath;
            String studentName = nameField.getText().trim(); // 获取学生姓名

            if ("男".equals(gender)) {
                imagePath = "/images/male.png";
                if (!studentName.isEmpty()) {
                    genderImageLabel.setText(studentName); // 显示学生姓名
                } else {
                    genderImageLabel.setText("男性学生");
                }
            } else if ("女".equals(gender)) {
                imagePath = "/images/female.png";
                if (!studentName.isEmpty()) {
                    genderImageLabel.setText(studentName); // 显示学生姓名
                } else {
                    genderImageLabel.setText("女性学生");
                }
            } else {
                genderImageView.setImage(null);
                genderImageLabel.setText("性别未知");
                return;
            }

            // 加载并显示图片
            Image image = new Image(imagePath);
            genderImageView.setImage(image);
        } catch (Exception e) {
            // 图片加载失败时处理
            e.printStackTrace();
            genderImageView.setImage(null);
            genderImageLabel.setText("图片加载失败");
        }
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadStudentData();
            return;
        }

        List<Student> filteredStudents = studentService.searchStudents(keyword);
        studentData.clear();
        studentData.addAll(filteredStudents);
        studentTable.setItems(studentData);
    }

    @FXML
    private void handleRefresh() {
        loadStudentData();
        loadMajorData(); // 重新加载专业数据，确保下拉框数据最新
        AlertUtil.showInfoAlert("刷新成功", "数据已刷新！");
    }

    @FXML
    private void clearSearch() {
        searchField.setText("");
        loadStudentData();
    }

    @FXML
    private void handleAdd() {
        String studentId = studentIdField.getText().trim();
        String name = nameField.getText().trim();
        String gender = genderComboBox.getValue();
        String majorName = majorComboBox.getValue();
        String className = classComboBox.getValue();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();

        if (studentId.isEmpty() || name.isEmpty() || gender == null ||
                majorName == null || className == null) {
            AlertUtil.showErrorAlert("输入错误", "请填写所有必填字段！");
            return;
        }

        try {
            Student student = new Student();
            student.setStudentId(studentId);
            student.setName(name);
            student.setGender(gender);
            student.setMajorName(majorName);
            student.setClassName(className);
            student.setPhone(phone);
            student.setEmail(email);

            int newId = studentService.addStudent(student);
            student.setId(newId);
            studentData.add(student);
            clearStudentDetails();
            AlertUtil.showInfoAlert("成功", "学生添加成功！");
        } catch (Exception e) {
            AlertUtil.showErrorAlert("错误", "添加学生失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdate() {
        Student selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        if (selectedStudent == null) {
            AlertUtil.showErrorAlert("选择错误", "请先选择一个学生进行修改！");
            return;
        }

        String studentId = studentIdField.getText().trim();
        String name = nameField.getText().trim();
        String gender = genderComboBox.getValue();
        String majorName = majorComboBox.getValue();
        String className = classComboBox.getValue();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();

        if (studentId.isEmpty() || name.isEmpty() || gender == null ||
                majorName == null || className == null) {
            AlertUtil.showErrorAlert("输入错误", "请填写所有必填字段！");
            return;
        }

        try {
            selectedStudent.setStudentId(studentId);
            selectedStudent.setName(name);
            selectedStudent.setGender(gender);
            selectedStudent.setMajorName(majorName);
            selectedStudent.setClassName(className);
            selectedStudent.setPhone(phone);
            selectedStudent.setEmail(email);

            studentService.updateStudent(selectedStudent);
            studentTable.refresh();
            AlertUtil.showInfoAlert("成功", "学生信息更新成功！");
        } catch (Exception e) {
            AlertUtil.showErrorAlert("错误", "更新学生失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML

    private void handleDelete() {
        Student selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        if (selectedStudent == null) {
            AlertUtil.showErrorAlert("选择错误", "请先选择一个学生进行删除！");
            return;
        }

        // 使用AlertUtil的确认对话框
        boolean confirmed = AlertUtil.showConfirmDialog(
                "确认删除",
                "你确定要删除这个学生吗？\n学生姓名: " + selectedStudent.getName()
        );

        if (confirmed) {
            try {
                studentService.deleteStudent(selectedStudent.getId());
                studentData.remove(selectedStudent);
                clearStudentDetails();
                AlertUtil.showInfoAlert("成功", "学生删除成功！");
            } catch (Exception e) {
                AlertUtil.showErrorAlert("错误", "删除学生失败：" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleResetForm() {
        clearStudentDetails();
    }

    @FXML
    private void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("确认退出");
        alert.setHeaderText("确定要退出登录吗？");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    System.out.println("管理员退出登录");

                    // 获取当前窗口 - 使用任意一个UI元素
                    Stage currentStage = (Stage) logoutButton.getScene().getWindow();

                    // 关闭当前窗口
                    currentStage.close();

                    // 注意：不需要再调用 mainApp.showLoginWindow()
                    // 因为窗口的关闭事件已经在 MainApp 中设置了

                } catch (Exception e) {
                    e.printStackTrace();
                    AlertUtil.showErrorAlert("错误", "退出登录失败：" + e.getMessage());
                }
            }
        });
    }

    /*private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    */
}