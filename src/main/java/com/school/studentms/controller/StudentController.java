package com.school.studentms.controller;

import com.school.studentms.MainApp;
import com.school.studentms.model.Student;
import com.school.studentms.model.Major;
import com.school.studentms.model.Clazz;
import com.school.studentms.service.StudentService;
import com.school.studentms.service.MajorService;
import com.school.studentms.service.ClassService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.util.List;
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
        showAlert("刷新成功", "数据已刷新！");
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
            showAlert("输入错误", "请填写所有必填字段！");
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
            showAlert("成功", "学生添加成功！");
        } catch (Exception e) {
            showAlert("错误", "添加学生失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdate() {
        Student selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        if (selectedStudent == null) {
            showAlert("选择错误", "请先选择一个学生进行修改！");
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
            showAlert("输入错误", "请填写所有必填字段！");
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
            showAlert("成功", "学生信息更新成功！");
        } catch (Exception e) {
            showAlert("错误", "更新学生失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDelete() {
        Student selectedStudent = studentTable.getSelectionModel().getSelectedItem();
        if (selectedStudent == null) {
            showAlert("选择错误", "请先选择一个学生进行删除！");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("确认删除");
        alert.setHeaderText("你确定要删除这个学生吗？");
        alert.setContentText("学生姓名: " + selectedStudent.getName());

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    studentService.deleteStudent(selectedStudent.getId());
                    studentData.remove(selectedStudent);
                    clearStudentDetails();
                    showAlert("成功", "学生删除成功！");
                } catch (Exception e) {
                    showAlert("错误", "删除学生失败：" + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}