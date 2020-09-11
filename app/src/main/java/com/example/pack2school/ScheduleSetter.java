package com.example.pack2school;

import java.util.List;

public class ScheduleSetter {
    List<String> Sunday;
    List<String> Monday;
    List<String> Tuesday;
    List<String> Wednesday;
    List<String> Thursday;
    List<String> Friday;
    String className;

    public ScheduleSetter(List<String> sundaySubjects, List<String> mondaySubjects, List<String> tuesdaySubjects,
                          List<String> wedensdaySubjects, List<String> thursdaySubjects, List<String> fridaySubjects,
                          String className) {
        this.Sunday = sundaySubjects;
        this.Monday = mondaySubjects;
        this.Tuesday = tuesdaySubjects;
        this.Wednesday = wedensdaySubjects;
        this.Thursday = thursdaySubjects;
        this.Friday = fridaySubjects;
        this.className = className;
    }
}
