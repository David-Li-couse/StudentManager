package com.school.studentms.controller;

import com.school.studentms.service.LoginService;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import com.school.studentms.MainApp;
import com.school.studentms.utils.AlertUtil;

public class LoginController {

    private MainApp mainApp;
    private boolean hasShownError = false;

    private LoginService loginService = new LoginService();

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private AnchorPane rootPane;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void initialize() {
        resetBackground();
        hasShownError = false;
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        String role = loginService.login(username, password);

        if ("admin".equals(role)) {
            handleLoginSuccess("admin", username);
        } else if ("student".equals(role)) {
            handleLoginSuccess("student", username);
        } else {
            handleLoginFailure();
        }
    }

    private void handleLoginSuccess(String role, String userId) {
        // 成功登录，关闭当前登录窗口，打开主界面
        Stage currentStage = (Stage) usernameField.getScene().getWindow();

        if (hasShownError) {
            resetBackgroundWithAnimation(() -> {
                // 动画完成后打开主界面并关闭登录窗口
                openMainStage(role, userId);
                currentStage.close();
            });
        } else {
            openMainStage(role, userId);
            currentStage.close();
        }
    }

    private void handleLoginFailure() {
        // 登录失败，使用动画切换为错误背景
        setErrorBackgroundWithAnimation();
        hasShownError = true;

        AlertUtil.showErrorAlert("登录失败", "用户名或密码错误！");
        passwordField.setText("");
    }

    private void openMainStage(String role, String userId) {
        System.out.println("openMainStage called, role = " + role + ", userId = " + userId);

        if (mainApp == null) {
            System.out.println("ERROR: mainApp is null!");
            showAlert("系统错误", "应用程序未正确初始化，请重启程序。");
            return;
        }

        Platform.runLater(() -> {
            if ("admin".equals(role)) {
                System.out.println("打开管理员界面");
                mainApp.showAdminMainStage();
            } else if ("student".equals(role)) {
                System.out.println("打开学生界面");
                mainApp.showStudentMainStage(userId);
            }
        });
    }

    // 设置错误背景（带动画）
    private void setErrorBackgroundWithAnimation() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(150), rootPane);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.3);

        fadeOut.setOnFinished(e -> {
            rootPane.setStyle(
                    "-fx-background-image: url('/images/loginerror-bg.png');" +
                            "-fx-background-size: cover;" +
                            "-fx-background-position: center;" +
                            "-fx-background-repeat: no-repeat;"
            );

            FadeTransition fadeIn = new FadeTransition(Duration.millis(150), rootPane);
            fadeIn.setFromValue(0.3);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });

        fadeOut.play();
    }

    // 恢复原始背景（带动画）带回调函数
    private void resetBackgroundWithAnimation(Runnable onFinished) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(150), rootPane);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.3);

        fadeOut.setOnFinished(e -> {
            resetBackground();

            FadeTransition fadeIn = new FadeTransition(Duration.millis(150), rootPane);
            fadeIn.setFromValue(0.3);
            fadeIn.setToValue(1.0);
            fadeIn.setOnFinished(event -> {
                if (onFinished != null) {
                    onFinished.run();
                }
            });
            fadeIn.play();
        });

        fadeOut.play();
    }

    // 立即恢复原始背景（无动画）
    private void resetBackground() {
        rootPane.setStyle(
                "-fx-background-image: url('/images/login-bg.png');" +
                        "-fx-background-size: cover;" +
                        "-fx-background-position: center;" +
                        "-fx-background-repeat: no-repeat;"
        );
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}