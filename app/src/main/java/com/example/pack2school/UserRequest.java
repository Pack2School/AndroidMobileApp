package com.example.pack2school;

import java.util.List;

public class UserRequest {
    private String userId;
    private String userName;
    private String userType;
    private String userEmail;
    private String userPassword;
    private List<String> childrenIds;
    private String subjectsTableName;
    private String classId;
    private String teacherUser;

    public UserRequest(String UserId, String UserName, String UserType, String UserEmail,
                       String UserPassword, List<String> ChildrenIds,
                       String SubjectsTableName, String ClassId, String TeacherUser){
        userId = UserId;
        userName = UserName;
        userType = UserType;
        userEmail = UserEmail;
        userPassword = UserPassword;
        childrenIds = ChildrenIds;
        subjectsTableName = SubjectsTableName;
        classId = ClassId;
        teacherUser = TeacherUser;
    }

    public String getClassId() { return classId; }

    public String getTeacherUser() { return teacherUser; }

    public List<String> getChildrenId() { return childrenIds; }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserType() {
        return userType;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public String getSubjectsTableName() {
        return subjectsTableName;
    }

}
