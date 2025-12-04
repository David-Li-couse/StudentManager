package com.school.studentms.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Optional;

public class AlertUtil {

    private static Image iconImage = null;

    static {
        try {
            // 加载图标 - 确保路径正确
            iconImage = new Image(AlertUtil.class.getResourceAsStream("/images/icon.png"));
            System.out.println("图标加载成功: " + iconImage);
        } catch (Exception e) {
            System.err.println("警告：无法加载图标文件 /images/icon.png");
            System.err.println("错误信息: " + e.getMessage());
        }
    }

    /**
     * 创建带自定义图标的Alert
     */
    private static Alert createAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        // 设置窗口图标
        setAlertIcon(alert);

        return alert;
    }

    /**
     * 为Alert设置图标
     */
    private static void setAlertIcon(Alert alert) {
        if (iconImage != null) {
            try {
                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                stage.getIcons().add(iconImage);
            } catch (Exception e) {
                System.err.println("设置Alert图标时出错: " + e.getMessage());
            }
        }
    }

    /**
     * 显示信息提示框
     */
    public static void showInfoAlert(String title, String content) {
        Alert alert = createAlert(Alert.AlertType.INFORMATION, title, null, content);
        alert.showAndWait();
    }

    /**
     * 显示错误提示框
     */
    public static void showErrorAlert(String title, String content) {
        Alert alert = createAlert(Alert.AlertType.ERROR, title, null, content);
        alert.showAndWait();
    }

    /**
     * 显示警告提示框
     */
    public static void showWarningAlert(String title, String content) {
        Alert alert = createAlert(Alert.AlertType.WARNING, title, null, content);
        alert.showAndWait();
    }

    /**
     * 显示确认对话框并返回结果
     */
    public static Optional<ButtonType> showConfirmAlert(String title, String content) {
        Alert alert = createAlert(Alert.AlertType.CONFIRMATION, title, null, content);
        return alert.showAndWait();
    }

    /**
     * 显示确认对话框（简化版）
     */
    public static boolean showConfirmDialog(String title, String content) {
        Optional<ButtonType> result = showConfirmAlert(title, content);
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}