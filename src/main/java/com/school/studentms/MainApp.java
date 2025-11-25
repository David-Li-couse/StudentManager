package com.school.studentms;

import com.school.studentms.controller.LoginController;
import com.school.studentms.controller.StudentController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class MainApp extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;
        showLoginWindow();
    }

    // 显示登录窗口
    public void showLoginWindow() throws IOException {
        URL fxmlUrl = getClass().getResource("/views/login.fxml");
        if (fxmlUrl == null) {
            showError("无法找到登录界面文件：login.fxml");
            return;
        }

        FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
        Scene scene = new Scene(fxmlLoader.load(), 400, 300);

        // 获取控制器并设置 MainApp 引用
        LoginController controller = fxmlLoader.getController();
        controller.setMainApp(this);

        primaryStage.setTitle("登录");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    // 登录成功后显示主界面

    public void showMainStage() {
        try {
            URL fxmlUrl = getClass().getResource("/views/StudentView.fxml");
            if (fxmlUrl == null) {
                showError("无法找到主界面文件：StudentView.fxml");
                return;
            }

            FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
            Scene scene = new Scene(fxmlLoader.load(), 1000, 700);

            // 获取控制器并设置 MainApp 引用
            StudentController controller = fxmlLoader.getController();
            controller.setMainApp(this);

            primaryStage.setTitle("学生管理系统");
            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("无法加载主界面：" + e.getMessage());
        }
    }

    // 显示专业班级管理界面
    public void showMajorClassManagement() {
        try {
            URL fxmlUrl = getClass().getResource("/views/MajorClassView.fxml");
            if (fxmlUrl == null) {
                showError("无法找到专业班级管理界面文件：MajorClassView.fxml");
                return;
            }

            FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
            Scene scene = new Scene(fxmlLoader.load(), 900, 600);
            Stage stage = new Stage();
            stage.setTitle("专业班级管理");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("无法加载专业班级管理界面：" + e.getMessage());
        }
    }

    // 显示课程管理界面
    public void showCourseManagement() {
        try {
            URL fxmlUrl = getClass().getResource("/views/CourseView.fxml");
            if (fxmlUrl == null) {
                showError("无法找到课程管理界面文件：CourseView.fxml");
                return;
            }

            FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
            Scene scene = new Scene(fxmlLoader.load(), 1000, 700);
            Stage stage = new Stage();
            stage.setTitle("课程管理");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("无法加载课程管理界面：" + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("错误");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }
}