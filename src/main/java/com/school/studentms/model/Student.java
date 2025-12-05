package com.school.studentms.model;

public class Student {
    private int id;
    private String studentId;
    private String name;
    private String gender;
    private String majorName;
    private String className;
    private String phone;
    private String email;
    private Double score;
    private String password;

    public Student() {}

    public Student(int id, String studentId, String name, String gender,
                   String majorName, String className, String phone, String email, Double score ,String password) {
        this.id = id;
        this.studentId = studentId;
        this.name = name;
        this.gender = gender;
        this.majorName = majorName;
        this.className = className;
        this.phone = phone;
        this.email = email;
        this.score = score;
        this.password = password;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getMajorName() { return majorName; }
    public void setMajorName(String majorName) { this.majorName = majorName; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}