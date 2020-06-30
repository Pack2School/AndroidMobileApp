package com.example.pack2school;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface JsonPlaceHolderApi {
    // instead of "posts" should be our ending of the url. for example if the base url is:
    // https://pack2schoolfunctions.azurewebsites.net/api/
    // then the below should be "negotiate"
    @GET("UpdateStudent")
    Call<List<NegotiatePost>> getPosts();

    @POST("negotiate")
    Call<NegotiateSignalROutput> getNegotiateConnectionFromSignalR(@Body NegotiateSignalRInput body);
}
