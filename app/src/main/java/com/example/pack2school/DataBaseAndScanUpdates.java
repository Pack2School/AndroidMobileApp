package com.example.pack2school;

import java.util.List;

public class DataBaseAndScanUpdates {
    private String studentId;
    private List<String> neededSubjects;
    private List<String> missingSubjects;
    private List<String> allSubjects;
    private String errorMessage;

    public List<String> getNeededSubjects() {
        return neededSubjects;
    }

    public List<String> getMissingSubjects() {
        return missingSubjects;
    }

    public List<String> getAllSubjects() {
        return allSubjects;
    }

    public String getErrorMessage() { return errorMessage; }
}
