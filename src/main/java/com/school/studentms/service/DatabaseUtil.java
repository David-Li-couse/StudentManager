package com.school.studentms.service;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseUtil {
    private static final String PROPERTIES_FILE = "application.properties";
    private static String url;
    private static String username;
    private static String password;

    // 静态代码块加载驱动和配置
    static {
        try {
            // 1. 加载 MySQL 驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL JDBC Driver loaded successfully.");

            // 2. 加载配置文件
            try (InputStream input = DatabaseUtil.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
                Properties prop = new Properties();
                if (input == null) {
                    throw new RuntimeException("无法找到配置文件 '" + PROPERTIES_FILE + "'");
                }
                prop.load(input);
                url = prop.getProperty("db.url");
                username = prop.getProperty("db.username");
                password = prop.getProperty("db.password");
            }
        } catch (ClassNotFoundException | IOException ex) {
            ex.printStackTrace();
            throw new ExceptionInInitializerError("初始化数据库连接失败: " + ex.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        System.out.println("Connecting to: " + url);
        return DriverManager.getConnection(url, username, password);
    }
}