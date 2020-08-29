package com.example.pack2school;

import java.util.List;

public class SubjectRequest {
    private String userId;
    private String tableName;
    private String subjectName;
    private String newSubjectName;
    private String requestType;
    private List<String> neededForTomorrow;
    private String stickerId;

    public SubjectRequest(String userId, String tableName, String subjectName, String newSubjectName, String requestType, List<String> neededForTomorrow, String stickerId) {
        this.userId = userId;
        this.tableName = tableName;
        this.subjectName = subjectName;
        this.newSubjectName = newSubjectName;
        this.requestType = requestType;
        this.neededForTomorrow = neededForTomorrow;
        this.stickerId = stickerId;
    }

    public String getUserId() {
        return userId;
    }

    public String getTableName() {
        return tableName;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String getNewSubjectName() {
        return newSubjectName;
    }

    public String getRequestType() {
        return requestType;
    }

    public List<String> getNeededForTomorrow() {
        return neededForTomorrow;
    }

    public String getStickerId() {
        return stickerId;
    }
}
