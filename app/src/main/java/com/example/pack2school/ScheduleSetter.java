package com.example.pack2school;

import java.util.List;

public class ScheduleSetter {
    List<String> sundaySubjects;
    List<String> mondaySubjects;
    List<String> tuesdaySubjects;
    List<String> wedensdaySubjects;
    List<String> thursdaySubjects;
    List<String> fridaySubjects;
    String teacherID;
    String className;

    public ScheduleSetter(List<String> sundaySubjects, List<String> mondaySubjects, List<String> tuesdaySubjects, List<String> wedensdaySubjects, List<String> thursdaySubjects, List<String> fridaySubjects, String teacherID, String className) {
        this.sundaySubjects = sundaySubjects;
        this.mondaySubjects = mondaySubjects;
        this.tuesdaySubjects = tuesdaySubjects;
        this.wedensdaySubjects = wedensdaySubjects;
        this.thursdaySubjects = thursdaySubjects;
        this.fridaySubjects = fridaySubjects;
        this.teacherID = teacherID;
        this.className = className;
    }
}
