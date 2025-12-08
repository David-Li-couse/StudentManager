package com.school.studentms;

import com.school.studentms.controller.LoginController;
import com.school.studentms.controller.StudentController;
import com.school.studentms.controller.StudentMainController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.scene.image.Image;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class MainApp extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;

        // 设置应用程序图标
        setApplicationIcon(primaryStage);

        // 显示登录窗口
        showLoginWindow();
    }

    // 设置应用程序图标
    private void setApplicationIcon(Stage stage) {
        try {
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

    // 获取应用程序图标
    private Image getApplicationIcon() {
        try {
            InputStream iconStream = getClass().getResourceAsStream("/images/icon.png");
            if (iconStream != null) {
                return new Image(iconStream);
            }
        } catch (Exception e) {
            System.err.println("加载图标失败: " + e.getMessage());
        }
        return null;
    }

    // 显示登录窗口（简化版本）
    public void showLoginWindow() {
        try {
            URL fxmlUrl = getClass().getResource("/views/login.fxml");
            if (fxmlUrl == null) {
                showError("无法找到登录界面文件：login.fxml");
                return;
            }

            FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);

            // 获取控制器并设置 MainApp 引用
            LoginController controller = fxmlLoader.getController();
            controller.setMainApp(this);

            // 创建新窗口用于登录
            Stage loginStage = new Stage();
            setApplicationIcon(loginStage);

            // 如果主舞台还未使用，设置为主舞台
            if (!primaryStage.isShowing()) {
                primaryStage = loginStage;
            }

            loginStage.setTitle("登录");
            loginStage.setScene(scene);
            loginStage.setResizable(false);
            loginStage.centerOnScreen();

            // 设置窗口关闭事件 - 直接退出程序（因为这是主窗口）
            loginStage.setOnCloseRequest(event -> {
                System.out.println("登录窗口被关闭，退出程序");
                Platform.exit();
                System.exit(0);
            });

            loginStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showError("无法加载登录界面：" + e.getMessage());
        }
    }

    // 显示管理员管理界面
    public void showAdminMainStage() {
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

            // 创建新窗口
            Stage adminStage = new Stage();
            setApplicationIcon(adminStage);

            adminStage.setTitle("管理员管理系统");
            adminStage.setScene(scene);
            adminStage.setResizable(true);

            // 窗口关闭时返回到登录界面
            adminStage.setOnCloseRequest(event -> {
                Platform.exit();
                System.exit(0);
            });

            adminStage.show();
            Platform.runLater(() -> adminStage.setMaximized(true));

        } catch (IOException e) {
            e.printStackTrace();
            showError("无法加载管理员界面：" + e.getMessage());
        }
    }

    // 显示学生管理界面
    public void showStudentMainStage(String studentId) {
        try {
            URL fxmlUrl = getClass().getResource("/views/student_main.fxml");
            if (fxmlUrl == null) {
                showError("无法找到主界面文件：student_main.fxml");
                return;
            }

            FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
            Scene scene = new Scene(fxmlLoader.load());

            // 获取控制器并设置 MainApp 引用
            StudentMainController controller = fxmlLoader.getController();
            controller.setStudentId(studentId);
            controller.setMainApp(this);

            // 创建新窗口
            Stage studentStage = new Stage();
            setApplicationIcon(studentStage);

            studentStage.setTitle("学生管理系统 - 学生端");
            studentStage.setScene(scene);
            studentStage.setResizable(true);

            // 窗口关闭时返回到登录界面
            studentStage.setOnCloseRequest(event -> {
                Platform.exit();
                System.exit(0);
            });

            studentStage.show();
            Platform.runLater(() -> studentStage.setMaximized(true));

        } catch (IOException e) {
            e.printStackTrace();
            showError("无法加载主界面：" + e.getMessage());
        }
    }

    // 应用程序退出方法
    @Override
    public void stop() {
        System.out.println("应用程序退出");
        Platform.exit();
        System.exit(0);
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
            stage.getIcons().add(getApplicationIcon());
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
            stage.getIcons().add(getApplicationIcon());
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