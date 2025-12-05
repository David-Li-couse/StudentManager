package com.school.studentms;

import com.school.studentms.controller.LoginController;
import com.school.studentms.controller.StudentController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
//UI
import javafx.scene.image.Image;
//UI

import java.io.IOException;

//UI
import java.io.InputStream;
//UI
import java.net.URL;

public class MainApp extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;

        // UI：设置应用程序图标
        setApplicationIcon(primaryStage);
        // UI：设置应用程序图标

        showLoginWindow();
    }

    //UI：新增方法：设置应用程序图标
    private void setApplicationIcon(Stage stage) {
        try {
            // 加载图标文件
            InputStream iconStream = getClass().getResourceAsStream("/images/icon.png");
            if (iconStream != null) {
                Image appIcon = new Image(iconStream);
                stage.getIcons().add(appIcon);
            } else {
                System.err.println("图标文件未找到，请检查路径");
            }
        } catch (Exception e) {
            System.err.println("加载图标失败: " + e.getMessage());
        }
    }
    //UI：新增方法：设置应用程序图标

    // UI：新增辅助方法获取图标
    private Image getApplicationIcon() {
        try {
            InputStream iconStream = getClass().getResourceAsStream("/images/icon.png");
            if (iconStream != null) {
                return new Image(iconStream);
            } else {
                System.err.println("图标文件未找到，请检查路径");
                return null;
            }
        } catch (Exception e) {
            System.err.println("加载图标失败: " + e.getMessage());
            return null;
        }
    }
    // UI：新增辅助方法获取图标

    // 显示登录窗口
    public void showLoginWindow() throws IOException {
        URL fxmlUrl = getClass().getResource("/views/login.fxml");
        if (fxmlUrl == null) {
            showError("无法找到登录界面文件：login.fxml");
            return;
        }

        FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
        //UI：修改登录窗口大小
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        //UI：修改登录窗口大小

        // 获取控制器并设置 MainApp 引用
        LoginController controller = fxmlLoader.getController();
        controller.setMainApp(this);

        //UI：修改icon
        primaryStage.getIcons().add(getApplicationIcon());
        //UI：修改icon
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
            Scene scene = new Scene(fxmlLoader.load());

            // 获取控制器并设置 MainApp 引用
            StudentController controller = fxmlLoader.getController();
            controller.setMainApp(this);

            //UI：修改icon
            primaryStage.getIcons().add(getApplicationIcon());
            //UI：修改icon
            primaryStage.setTitle("学生管理系统");
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.show();
            Platform.runLater(() -> {
                primaryStage.setMaximized(true);
            });

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

            //UI：修改icon
            Stage stage = new Stage();
            stage.getIcons().add(getApplicationIcon()); // 为新窗口设置图标
            //UI：修改icon

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

            // UI：修改icon - 添加图标设置
            Stage stage = new Stage();
            stage.getIcons().add(getApplicationIcon()); // 为新窗口设置图标
            // UI：修改icon

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