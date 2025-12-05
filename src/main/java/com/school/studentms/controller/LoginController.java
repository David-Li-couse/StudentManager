package com.school.studentms.controller;

import com.school.studentms.service.LoginService;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import com.school.studentms.MainApp;
import com.school.studentms.utils.AlertUtil;
import com.school.studentms.service.LoginService;

public class LoginController {

    private MainApp mainApp;
    private boolean hasShownError = false; // 标记是否显示过错误背景

    private LoginService loginservice = new LoginService();

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private AnchorPane rootPane; // 添加对AnchorPane的引用

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    // 初始化方法
    @FXML
    private void initialize() {
        // 确保初始背景是正确的
        resetBackground();
        hasShownError = false; // 重置标记
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        boolean login_admin = loginservice.adminLogin(username, password);
        boolean login_student = loginservice.studentLogin(username, password);

        if (login_admin) {
            // 管理员登录成功
            if (hasShownError) {
                // 如果之前显示过错误背景，使用动画恢复
                resetBackgroundWithAnimation(() -> openMainStage());
            } else {
                // 首次正确登录，直接打开主界面
                openMainStage();
            }

        }
        else if(login_student) {
            // 学生登录成功
            if (hasShownError) {
                // 如果之前显示过错误背景，使用动画恢复
                resetBackgroundWithAnimation(() -> openMainStage());
            } else {
                // 首次正确登录，直接打开主界面
                openMainStage();
            }
        }
        else {
            // 登录失败，使用动画切换为错误背景
            setErrorBackgroundWithAnimation();
            hasShownError = true; // 标记已显示错误背景

            // 显示错误提示 - 使用AlertUtil的正确方法
            AlertUtil.showErrorAlert("登录失败", "用户名或密码错误！");

            // 清空密码框
            passwordField.setText("");
        }
    }

    // 打开主界面的方法
    private void openMainStage() {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();

        if (mainApp != null) {
            mainApp.showMainStage();
        } else {
            AlertUtil.showErrorAlert("错误", "应用程序引用未设置");
        }
    }

    // 设置错误背景（带动画）
    private void setErrorBackgroundWithAnimation() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(150), rootPane);
        fadeOut.setFromValue(1.0);   // 从完全不透明
        fadeOut.setToValue(0.3);     // 到30%透明度

        fadeOut.setOnFinished(e -> {
            // 在淡出完成后，切换背景图片
            rootPane.setStyle(
                    "-fx-background-image: url('/images/loginerror-bg.png');" +
                            "-fx-background-size: cover;" +
                            "-fx-background-position: center;" +
                            "-fx-background-repeat: no-repeat;"
            );

            // 淡入动画
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
            // 切换回正常背景
            rootPane.setStyle(
                    "-fx-background-image: url('/images/login-bg.png');" +
                            "-fx-background-size: cover;" +
                            "-fx-background-position: center;" +
                            "-fx-background-repeat: no-repeat;"
            );

            // 淡入动画
            FadeTransition fadeIn = new FadeTransition(Duration.millis(150), rootPane);
            fadeIn.setFromValue(0.3);
            fadeIn.setToValue(1.0);
            fadeIn.setOnFinished(event -> {
                // 动画完成后执行回调
                if (onFinished != null) {
                    onFinished.run();
                }
            });
            fadeIn.play();
        });

        fadeOut.play();
    }

    // 立即设置错误背景（无动画）
    private void setErrorBackground() {
        rootPane.setStyle(
                "-fx-background-image: url('/images/loginerror-bg.png');" +
                        "-fx-background-size: cover;" +
                        "-fx-background-position: center;" +
                        "-fx-background-repeat: no-repeat;"
        );
        hasShownError = true;
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