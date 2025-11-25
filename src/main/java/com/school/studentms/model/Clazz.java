package com.school.studentms.model;

public class Clazz {
    private int classCode;
    private String className;
    private String majorCode;

    public Clazz() {}

    public Clazz(int classCode, String className, String majorCode) {
        this.classCode = classCode;
        this.className = className;
        this.majorCode = majorCode;
    }

    public int getClassCode() { return classCode; }
    public void setClassCode(int classCode) { this.classCode = classCode; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public String getMajorCode() { return majorCode; }
    public void setMajorCode(String majorCode) { this.majorCode = majorCode; }
}