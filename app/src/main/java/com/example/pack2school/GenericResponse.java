package com.example.pack2school;

public class GenericResponse {
    private String errorMessage;
    private boolean requestSucceeded;
    private Object data;

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean didRequestSucceed() {
        return requestSucceeded;
    }

    public Object getData() {
        return data;
    }
}
