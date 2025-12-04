package com.school.studentms.controller;

import com.school.studentms.MainApp;
import com.school.studentms.model.Major;
import com.school.studentms.model.Clazz;
import com.school.studentms.service.MajorService;
import com.school.studentms.service.ClassService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import com.school.studentms.utils.AlertUtil;

import java.util.List;

public class MajorClassController {
    @FXML private AnchorPane majorClassView;

    // 专业相关控件
    @FXML private TableView<Major> majorTable;
    @FXML private TableColumn<Major, String> colMajorCode;
    @FXML private TableColumn<Major, String> colMajorName;
    @FXML private TextField majorCodeField;
    @FXML private TextField majorNameField;
    @FXML private Button addMajorButton;
    @FXML private Button updateMajorButton;
    @FXML private Button deleteMajorButton;

    // 班级相关控件
    @FXML private TableView<Clazz> classTable;
    @FXML private TableColumn<Clazz, Integer> colClassCode;
    @FXML private TableColumn<Clazz, String> colClassName;
    @FXML private TableColumn<Clazz, String> colClassMajor;
    @FXML private TextField classNameField;
    @FXML private ComboBox<String> classMajorComboBox;
    @FXML private Button addClassButton;
    @FXML private Button updateClassButton;
    @FXML private Button deleteClassButton;

    private final MajorService majorService = new MajorService();
    private final ClassService classService = new ClassService();
    private ObservableList<Major> majorData = FXCollections.observableArrayList();
    private ObservableList<Clazz> classData = FXCollections.observableArrayList();
    private ObservableList<String> majorComboData = FXCollections.observableArrayList();
    private MainApp mainApp;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void handleBackToStudent() {
        // 关闭当前窗口
        Stage stage = (Stage) majorClassView.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void initialize() {
        // 初始化专业表格
        colMajorCode.setCellValueFactory(new PropertyValueFactory<>("majorCode"));
        colMajorName.setCellValueFactory(new PropertyValueFactory<>("majorName"));

        // 初始化班级表格
        colClassCode.setCellValueFactory(new PropertyValueFactory<>("classCode"));
        colClassName.setCellValueFactory(new PropertyValueFactory<>("className"));
        colClassMajor.setCellValueFactory(new PropertyValueFactory<>("majorCode"));

        // 加载数据
        loadMajorData();
        loadClassData();
        loadMajorComboData();

        // 设置表格选择监听器
        majorTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showMajorDetails(newValue));

        classTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showClassDetails(newValue));
    }

    private void loadMajorData() {
        majorData.clear();
        majorData.addAll(majorService.getAllMajors());
        majorTable.setItems(majorData);
    }

    private void loadClassData() {
        classData.clear();
        classData.addAll(classService.getAllClasses());
        classTable.setItems(classData);
    }

    private void loadMajorComboData() {
        majorComboData.clear();
        List<Major> majors = majorService.getAllMajors();
        for (Major major : majors) {
            majorComboData.add(major.getMajorCode());
        }
        classMajorComboBox.setItems(majorComboData);
    }

    @FXML
    private void handleRefreshMajors() {
        loadMajorData();
        loadMajorComboData(); // 刷新专业下拉框
        AlertUtil.showInfoAlert("刷新成功", "专业数据已刷新！");
    }

    @FXML
    private void handleRefreshClasses() {
        loadClassData();
        AlertUtil.showInfoAlert("刷新成功", "班级数据已刷新！");
    }

    private void showMajorDetails(Major major) {
        if (major != null) {
            majorCodeField.setText(major.getMajorCode());
            majorNameField.setText(major.getMajorName());
        } else {
            clearMajorDetails();
        }
    }

    private void showClassDetails(Clazz clazz) {
        if (clazz != null) {
            classNameField.setText(clazz.getClassName());
            classMajorComboBox.setValue(clazz.getMajorCode());
        } else {
            clearClassDetails();
        }
    }

    private void clearMajorDetails() {
        majorCodeField.setText("");
        majorNameField.setText("");
    }

    private void clearClassDetails() {
        classNameField.setText("");
        classMajorComboBox.setValue(null);
    }

    // 专业操作方法
    @FXML
    private void handleAddMajor() {
        String majorCode = majorCodeField.getText().trim();
        String majorName = majorNameField.getText().trim();

        if (majorCode.isEmpty() || majorName.isEmpty()) {
            AlertUtil.showErrorAlert("输入错误", "请填写所有专业字段！");
            return;
        }

        if (majorService.isMajorCodeExists(majorCode)) {
            AlertUtil.showErrorAlert("输入错误", "专业代码已存在！");
            return;
        }

        if (majorService.isMajorNameExists(majorName)) {
            AlertUtil.showErrorAlert("输入错误", "专业名称已存在！");
            return;
        }

        try {
            Major major = new Major(majorCode, majorName);
            boolean success = majorService.addMajor(major);
            if (success) {
                majorData.add(major);
                loadMajorComboData(); // 刷新专业下拉框
                clearMajorDetails();
                AlertUtil.showInfoAlert("成功", "专业添加成功！");
            }
        } catch (Exception e) {
            AlertUtil.showErrorAlert("错误", "添加专业失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdateMajor() {
        Major selectedMajor = majorTable.getSelectionModel().getSelectedItem();
        if (selectedMajor == null) {
            AlertUtil.showErrorAlert("选择错误", "请先选择一个专业进行修改！");
            return;
        }

        String majorCode = majorCodeField.getText().trim();
        String majorName = majorNameField.getText().trim();

        if (majorCode.isEmpty() || majorName.isEmpty()) {
            AlertUtil.showErrorAlert("输入错误", "请填写所有专业字段！");
            return;
        }

        // 检查名称是否与其他专业重复
        if (!selectedMajor.getMajorName().equals(majorName) &&
                majorService.isMajorNameExists(majorName)) {
            AlertUtil.showErrorAlert("输入错误", "专业名称已存在！");
            return;
        }

        try {
            selectedMajor.setMajorCode(majorCode);
            selectedMajor.setMajorName(majorName);
            boolean success = majorService.updateMajor(selectedMajor);
            if (success) {
                majorTable.refresh();
                loadMajorComboData(); // 刷新专业下拉框
                AlertUtil.showInfoAlert("成功", "专业信息更新成功！");
            }
        } catch (Exception e) {
            AlertUtil.showErrorAlert("错误", "更新专业失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteMajor() {
        Major selectedMajor = majorTable.getSelectionModel().getSelectedItem();
        if (selectedMajor == null) {
            AlertUtil.showErrorAlert("选择错误", "请先选择一个专业进行删除！");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("确认删除");
        alert.setHeaderText("你确定要删除这个专业吗？");
        alert.setContentText("专业名称: " + selectedMajor.getMajorName());

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    boolean success = majorService.deleteMajor(selectedMajor.getMajorCode());
                    if (success) {
                        majorData.remove(selectedMajor);
                        loadMajorComboData(); // 刷新专业下拉框
                        clearMajorDetails();
                        showAlert("成功", "专业删除成功！");
                    }
                } catch (Exception e) {
                    showAlert("错误", "删除专业失败：" + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    // 班级操作方法
    @FXML
    private void handleAddClass() {
        String className = classNameField.getText().trim();
        String majorCode = classMajorComboBox.getValue();

        if (className.isEmpty() || majorCode == null) {
            AlertUtil.showErrorAlert("输入错误", "请填写所有班级字段！");
            return;
        }

        if (classService.isClassNameExists(className)) {
            AlertUtil.showErrorAlert("输入错误", "班级名称已存在！");
            return;
        }

        try {
            Clazz clazz = new Clazz();
            clazz.setClassName(className);
            clazz.setMajorCode(majorCode);
            boolean success = classService.addClass(clazz);
            if (success) {
                // 重新加载获取包含ID的班级数据
                loadClassData();
                clearClassDetails();
                AlertUtil.showInfoAlert("成功", "班级添加成功！");
            }
        } catch (Exception e) {
            AlertUtil.showErrorAlert("错误", "添加班级失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdateClass() {
        Clazz selectedClass = classTable.getSelectionModel().getSelectedItem();
        if (selectedClass == null) {
            AlertUtil.showErrorAlert("选择错误", "请先选择一个班级进行修改！");
            return;
        }

        String className = classNameField.getText().trim();
        String majorCode = classMajorComboBox.getValue();

        if (className.isEmpty() || majorCode == null) {
            AlertUtil.showErrorAlert("输入错误", "请填写所有班级字段！");
            return;
        }

        // 检查名称是否与其他班级重复
        if (!selectedClass.getClassName().equals(className) &&
                classService.isClassNameExists(className)) {
            AlertUtil.showErrorAlert("输入错误", "班级名称已存在！");
            return;
        }

        try {
            selectedClass.setClassName(className);
            selectedClass.setMajorCode(majorCode);
            boolean success = classService.updateClass(selectedClass);
            if (success) {
                classTable.refresh();
                AlertUtil.showInfoAlert("成功", "班级信息更新成功！");
            }
        } catch (Exception e) {
            AlertUtil.showErrorAlert("错误", "更新班级失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteClass() {
        Clazz selectedClass = classTable.getSelectionModel().getSelectedItem();
        if (selectedClass == null) {
            AlertUtil.showErrorAlert("选择错误", "请先选择一个班级进行删除！");
            return;
        }

        // 使用AlertUtil的确认对话框
        boolean confirmed = AlertUtil.showConfirmDialog(
                "确认删除",
                "你确定要删除这个班级吗？\n班级名称: " + selectedClass.getClassName()
        );

        if (confirmed) {
            try {
                boolean success = classService.deleteClass(selectedClass.getClassCode());
                if (success) {
                    classData.remove(selectedClass);
                    clearClassDetails();
                    AlertUtil.showInfoAlert("成功", "班级删除成功！");
                } else {
                    AlertUtil.showErrorAlert("错误", "班级删除失败！");
                }
            } catch (Exception e) {
                AlertUtil.showErrorAlert("错误", "删除班级失败：" + e.getMessage());
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