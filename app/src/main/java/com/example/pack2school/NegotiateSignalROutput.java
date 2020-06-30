package com.example.pack2school;

import io.reactivex.Single;

public class NegotiateSignalROutput {
    // the names of the values should be the same as returned from the backend!
    private String accessToken;
    private String url;

    public String getAccessToken() {return accessToken;}
    public String getUrl() {return url;}
}
