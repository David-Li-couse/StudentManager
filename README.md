#项目名称
  StudentManager

##项目描述
  大学生课程设计作业，实现学生信息管理系统，主要功能包括：登录；学生信息增删改查；专业班级增删改；课程信息增删改查；为学生安排课程；录入成绩、、、、、、

##开发环境要求
  开发语言：Java
  JDK版本：21.0.6
  Maven版本：3.6.1
  Javafx版本：21.0.9
  数据库：MySQL 8.0

#测试报告模板
```
测试报告
【日期】-【姓名】
一、建议（可增加的功能/小修改意见）：
1、xxx
2、


二、报错（功能不完善/运行错误）：
1、名称：【xxx】重现步骤：【xxx】报错信息：【xxx】
2、
```


#github同步项目的教程https://www.bilibili.com/video/BV1o7411U7j6?t=6.1
#tip：同步项目后要修改application.properties中的数据库密码

#上手指南
安装正确版本的JDK，Maven，Javafx
1、将/src/main/rescourse/mysql.sql脚本在你的mysql上执行,在/src/main/resourse/application.properties中修改数据库连接信息
2、根据配置更改版本号
3、在idea中打开项目文件StudentManager
4、在idea最顶端的运行按钮的左边有个向下的小三角，点击小三角，点击编辑配置：点击添加，选择应用程序/Application，名称填写MainApp，构建并运行选择java版本（推荐21.0.6），主类填写com.school.studentms.MainApp，点击修改选项，选择添加虚拟机选项并填写--module-path "【Java的lib路径】" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base环境变量填写CLASSPATH=target/classes，点击应用，再点击确定
5、点击文件，选择项目结构，在项目设置中点击项目，在SDK一栏选择java版本，点击模块，在SDK一栏选择java版本；在平台设置中点击SDK，选择java版本，点击应用，点击确定
6、同步maven项目

如果java版本不是21（如23），要额外更改pom.xml如下：
```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                             http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.school</groupId>
    <artifactId>studentms-desktop</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>23</maven.compiler.source>
        <maven.compiler.target>23</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <javafx.version>21.0.9</javafx.version>
    </properties>

    <dependencies>
        <!-- JavaFX -->
        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-controls</artifactId>
            <version>${javafx.version}</version>
        </dependency>
        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-fxml</artifactId>
            <version>${javafx.version}</version>
        </dependency>
        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-base</artifactId>
            <version>${javafx.version}</version>
        </dependency>

        <!-- MySQL JDBC Driver -->
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <version>8.0.33</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <source>23</source>
                    <target>23</target>
                </configuration>
            </plugin>

            <!-- 使用正确的 JavaFX 插件版本 -->
            <plugin>
                <groupId>org.openjfx</groupId>
                <artifactId>javafx-maven-plugin</artifactId>
                <version>0.0.8</version>
                <configuration>
                    <mainClass>com.school.studentms.MainApp</mainClass>
                    <options>
                        <option>--module-path</option>
                        <option>D:\Software\javafx-sdk-21.0.9\lib</option>
                        <option>--add-modules</option>
                        <option>javafx.controls,javafx.fxml,javafx.base</option>
                    </options>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```
