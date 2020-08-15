package com.example.pack2school;

public class Tuple<B, S> {
    public final Boolean succeeded;
    public final String error_message;

    public Tuple(Boolean succeeded, String error_message) {
        this.succeeded = succeeded;
        this.error_message = error_message;
    }

    public Boolean getSucceeded() {
        return succeeded;
    }

    public String getError_message() {
        return error_message;
    }
}
