                                        -- 创建数据库
CREATE DATABASE IF NOT EXISTS student_db0 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE student_db0;

-- 创建专业表
CREATE TABLE IF NOT EXISTS majors (
    major_code VARCHAR(20) PRIMARY KEY,
    major_name VARCHAR(100) NOT NULL UNIQUE
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4;

-- 创建班级表
CREATE TABLE IF NOT EXISTS classes (
    class_code INT AUTO_INCREMENT PRIMARY KEY,
    class_name VARCHAR(100) NOT NULL UNIQUE,
    major_code VARCHAR(20) NOT NULL,
    FOREIGN KEY (major_code) REFERENCES majors(major_code) ON DELETE CASCADE
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4;

-- 创建课程表
CREATE TABLE IF NOT EXISTS courses (
    course_code VARCHAR(20) PRIMARY KEY,
    course_name VARCHAR(100) NOT NULL UNIQUE
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4;

-- 创建学生表
CREATE TABLE IF NOT EXISTS students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(50) NOT NULL,
    gender ENUM('男', '女') NOT NULL,
    major_name VARCHAR(100) NOT NULL,
    class_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (major_name) REFERENCES majors(major_name) ON DELETE CASCADE,
    FOREIGN KEY (class_name) REFERENCES classes(class_name) ON DELETE CASCADE
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4;

-- 插入初始专业数据
INSERT IGNORE INTO majors (major_code, major_name) VALUES
('CS', '计算机科学'),
('EE', '电子工程'),
('MA', '数学'),
('PH', '物理'),
('CH', '化学');

-- 插入初始班级数据
INSERT IGNORE INTO classes (class_name, major_code) VALUES
('计算机1班', 'CS'),
('计算机2班', 'CS'),
('计算机3班', 'CS'),
('电子1班', 'EE'),
('电子2班', 'EE'),
('数学1班', 'MA'),
('数学2班', 'MA'),
('物理1班', 'PH'),
('化学1班', 'CH');

-- 插入初始课程数据（修复：添加分号）
INSERT IGNORE INTO courses (course_code, course_name) VALUES
('CS101', '程序设计基础'),
('CS102', '数据结构'),
('MA101', '高等数学');

-- 插入示例学生数据
INSERT IGNORE INTO students (student_id, name, gender, major_name, class_name, phone, email) VALUES
('20230001', '张三', '男', '计算机科学', '计算机1班', '13800138001', 'zhangsan@email.com'),
('20230002', '李四', '女', '计算机科学', '计算机1班', '13800138002', 'lisi@email.com'),
('20230003', '王五', '男', '计算机科学', '计算机2班', '13800138003', 'wangwu@email.com'),
('20230004', '赵六', '女', '电子工程', '电子1班', '13800138004', 'zhaoliu@email.com'),
('20230005', '钱七', '男', '数学', '数学1班', '13800138005', 'qianqi@email.com'),
('20230006', '孙八', '女', '物理', '物理1班', '13800138006', 'sunba@email.com'),
('20230007', '周九', '男', '化学', '化学1班', '13800138007', 'zhoujiu@email.com'),
('20230008', '吴十', '女', '计算机科学', '计算机3班', '13800138008', 'wushi@email.com');

-- 创建程序设计基础课程表
CREATE TABLE IF NOT EXISTS course_CS101 (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(20) NOT NULL,
    student_name VARCHAR(50) NOT NULL,
    gender ENUM('男', '女') NOT NULL,
    major_name VARCHAR(100) NOT NULL,
    score DECIMAL(5,2) DEFAULT NULL,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4;

-- 为程序设计基础课程分配学生
INSERT IGNORE INTO course_CS101 (student_id, student_name, gender, major_name) VALUES
('20230001', '张三', '男', '计算机科学'),
('20230002', '李四', '女', '计算机科学'),
('20230003', '王五', '男', '计算机科学'),
('20230008', '吴十', '女', '计算机科学');

-- 创建数据结构课程表
CREATE TABLE IF NOT EXISTS course_CS102 (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(20) NOT NULL,
    student_name VARCHAR(50) NOT NULL,
    gender ENUM('男', '女') NOT NULL,
    major_name VARCHAR(100) NOT NULL,
    score DECIMAL(5,2) DEFAULT NULL,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4;

-- 为数据结构课程分配学生
INSERT IGNORE INTO course_CS102 (student_id, student_name, gender, major_name) VALUES
('20230001', '张三', '男', '计算机科学'),
('20230002', '李四', '女', '计算机科学'),
('20230008', '吴十', '女', '计算机科学');

-- 创建高等数学课程表
CREATE TABLE IF NOT EXISTS course_MA101 (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(20) NOT NULL,
    student_name VARCHAR(50) NOT NULL,
    gender ENUM('男', '女') NOT NULL,
    major_name VARCHAR(100) NOT NULL,
    score DECIMAL(5,2) DEFAULT NULL,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4;

-- 为高等数学课程分配学生
INSERT IGNORE INTO course_MA101 (student_id, student_name, gender, major_name) VALUES
('20230001', '张三', '男', '计算机科学'),
('20230002', '李四', '女', '计算机科学'),
('20230003', '王五', '男', '计算机科学'),
('20230005', '钱七', '男', '数学'),
('20230008', '吴十', '女', '计算机科学');

-- 更新部分学生的成绩作为示例
UPDATE course_CS101 SET score = 85.5 WHERE student_id = '20230001';
UPDATE course_CS101 SET score = 92.0 WHERE student_id = '20230002';
UPDATE course_CS101 SET score = 78.5 WHERE student_id = '20230003';
UPDATE course_CS101 SET score = 88.0 WHERE student_id = '20230008';

UPDATE course_CS102 SET score = 90.0 WHERE student_id = '20230001';
UPDATE course_CS102 SET score = 87.5 WHERE student_id = '20230002';
UPDATE course_CS102 SET score = 82.0 WHERE student_id = '20230008';

UPDATE course_MA101 SET score = 95.0 WHERE student_id = '20230001';
UPDATE course_MA101 SET score = 89.5 WHERE student_id = '20230002';
UPDATE course_MA101 SET score = 76.0 WHERE student_id = '20230003';
UPDATE course_MA101 SET score = 98.5 WHERE student_id = '20230005';
UPDATE course_MA101 SET score = 84.0 WHERE student_id = '20230008';

-- 显示创建的表信息
SHOW TABLES;

-- 显示各表的记录数
SELECT
    'majors' AS table_name,
    COUNT(*) AS record_count
FROM majors
UNION ALL
SELECT
    'classes' AS table_name,
    COUNT(*) AS record_count
FROM classes
UNION ALL
SELECT
    'courses' AS table_name,
    COUNT(*) AS record_count
FROM courses
UNION ALL
SELECT
    'students' AS table_name,
    COUNT(*) AS record_count
FROM students
UNION ALL
SELECT
    'course_CS101' AS table_name,
    COUNT(*) AS record_count
FROM course_CS101
UNION ALL
SELECT
    'course_CS102' AS table_name,
    COUNT(*) AS record_count
FROM course_CS102
UNION ALL
SELECT
    'course_MA101' AS table_name,
    COUNT(*) AS record_count
FROM course_MA101;