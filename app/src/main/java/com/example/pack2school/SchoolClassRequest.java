package com.example.pack2school;

public class SchoolClassRequest {
    private String teacherId;
    private String classId;

    public SchoolClassRequest(String teacherId, String classId) {
        this.teacherId = teacherId;
        this.classId = classId;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public String getClassId() {
        return classId;
    }
}
