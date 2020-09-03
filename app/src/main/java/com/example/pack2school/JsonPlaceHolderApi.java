package com.example.pack2school;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;


public interface JsonPlaceHolderApi {
    // For example if the base url is:
    // https://pack2schoolfunctions.azurewebsites.net/api/
    // then if the below is "negotiate", we will access the "negotiate" method in the above base url.

    @POST("negotiate")
    Call<NegotiateSignalROutput> getNegotiateConnectionFromSignalR(@Body NegotiateSignalRInput body);

    @POST("SignUp")
    Call<GenericResponse> signUp(@Body UserRequest body);

    @POST("SignIn")
    Call<GenericResponse> signIn(@Body UserRequest body);

    @POST("GetChildSubjectsTableName")
    Call<GenericResponse> GetStudentClassTableName(@Body UserRequest body);

    @POST("GetNeededSubjects")
    Call<GenericResponse> GetNeededSubjects(@Body SubjectRequest body);

    @POST("GetMissingSubjects")
    Call<GenericResponse> GetMissingSubjects(@Body SubjectRequest body);

    @POST("AddNewClass")
    Call<GenericResponse> AddNewClass(@Body SchoolClassRequest body);

    @POST("GetAllSubjects")
    Call<GenericResponse> GetAllSubjects(@Body SubjectRequest body);

    @POST("EditSubject")
    Call<GenericResponse> EditSubject(@Body SubjectRequest body);

    @POST("UpdateSubjectNecessity")
    Call<GenericResponse> UpdateSubjectNecessity(@Body SubjectRequest body);

    @POST("SendScanOperation")
    Call<GenericResponse> SendScanOperation(@Body SubjectRequest body);

    @POST("UpdateSubjectStickers")
    Call<GenericResponse> UpdateSubjectStickers(@Body SubjectRequest body);
}
