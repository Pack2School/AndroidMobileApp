package com.example.pack2school;

public class NegotiateSignalRInput {
    // the names of the values should be the same as returned from the backend!
    private String UserId;
    private String DeviceId;

    public NegotiateSignalRInput(String userId, String deviceId) {
        UserId = userId;
        DeviceId = deviceId;
    }
}
