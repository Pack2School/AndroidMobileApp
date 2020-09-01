package com.example.pack2school;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.microsoft.signalr.Action1;
import com.microsoft.signalr.HubConnection;

import java.util.List;


public class student_main_page extends AppCompatActivity {

    Intent myIntent;
    String my_user_id_as_string;
    String my_name_as_string;
    String my_class_name_as_string;
    String entrance_type;
    String device_connection_string;
    TextView welcome_text_view;
    TextView all_subjects_text_view;
    TextView needed_subjects_text_view;
    TextView missing_subjects_text_view;
    Button scan_my_bag_btn;
    Button set_sticker_btn;
    HubConnection hubConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main_page);

        myIntent = getIntent();
        my_user_id_as_string = myIntent.getStringExtra(MainActivity.USER_ID);
        my_name_as_string = myIntent.getStringExtra(MainActivity.NAME);
        entrance_type = myIntent.getStringExtra(MainActivity.ENTRANCE_TYPE);
        device_connection_string = myIntent.getStringExtra(MainActivity.DEVICE_CONNECTION_STRING);
        my_class_name_as_string = myIntent.getStringExtra(MainActivity.CLASSES_IDS);

        all_subjects_text_view = (TextView)findViewById(R.id.all_subjects_text_view);
        needed_subjects_text_view = (TextView)findViewById(R.id.needed_subjects_text_view);
        missing_subjects_text_view = (TextView)findViewById(R.id.missing_subjects_text_view);

        welcome_text_view = (TextView)findViewById(R.id.welcome_text_view);
        if(entrance_type.equals(MainActivity.SIGN_UP)){
            welcome_text_view.setText("Welcome " + my_name_as_string + ". \nPlease follow the instructions you got in school and set the above string to your scanner:\n" + device_connection_string);
        }
        else{
            welcome_text_view.setText("Welcome " + my_name_as_string);
        }

        scan_my_bag_btn = (Button)findViewById(R.id.scan_my_bag_btn);
        set_sticker_btn = (Button)findViewById(R.id.set_sticker_btn);

        // Get latest data from cloud and show it to user
        show_all_subjects();
        show_needed_subjects();
        show_missing_subjects();
        connect_to_signalR_and_wait_for_updates(); // in this method we also automatically trigger a scan - which in turn will trigger the method we are listening to.


        scan_my_bag_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.call_backpack_scan_op(my_user_id_as_string);
            }
        });

        set_sticker_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO - open window/popup for entering sticker name and choosing a book for it
            }
        });
    }

    private void connect_to_signalR_and_wait_for_updates(){
        Call<NegotiateSignalROutput> signalR_call = MainActivity.get_signalR_connection_call(my_user_id_as_string);
        signalR_call.enqueue(new Callback<NegotiateSignalROutput>() {
            @Override
            public void onResponse(Call<NegotiateSignalROutput> call, Response<NegotiateSignalROutput> response) {
                if (!response.isSuccessful()) {
                    System.out.println("Error occurred in enqueueing a negotiate call. Error code: " + response.code());
                    return;
                }
                hubConnection = MainActivity.get_hubconnection_from_successful_negotiate_response(response);
                hubConnection.start();
                hubConnection.on(MainActivity.DataBaseAndScanUpdates, new Action1<Object>() {
                    @Override
                    public void invoke(Object param1) {
                        System.out.println(MainActivity.DataBaseAndScanUpdates + " was invoked!");
                        DataBaseAndScanUpdates params = (DataBaseAndScanUpdates) param1;  //cast the object to our expected DataBaseAndScanUpdates class
                        System.out.println("Input from method: " + params);
                        List<String> all_subjects = params.getAllSubjects();
                        List<String> needed_subjects = params.getNeededSubjects();
                        List<String> missing_subjects = params.getMissingSubjects();
                        if(all_subjects != null){
                            set_all_subjects(all_subjects);
                        }
                        if(needed_subjects != null){
                            set_all_subjects(needed_subjects);
                        }
                        if(missing_subjects != null){
                            set_all_subjects(missing_subjects);
                        }
                    }
                }, Object.class);
            }
            @Override
            public void onFailure(Call<NegotiateSignalROutput> call, Throwable t) {
                System.out.println("Enqueueing a negotiate call failed! Failure message: \n" + t.getMessage());
            }
        });
    }

    private void show_all_subjects(){
        JsonPlaceHolderApi jsonPlaceHolderApi = MainActivity.getRetrofitJsonPlaceHolderApi();
        SubjectRequest edit_class = new SubjectRequest(null,
                my_class_name_as_string,
                null,
                null,
                null,
                null,
                null);
        Call<GenericResponse> edit_class_response = jsonPlaceHolderApi.GetAllSubjects(edit_class);
        edit_class_response.enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                Tuple get_subjects_result = MainActivity.log_request_errors(response, MainActivity.TEACHER, MainActivity.GET_ALL_SUBJECTS);
                if (get_subjects_result.getSucceeded()){
                    GenericResponse request_response = response.body();
                    System.out.println("Entire response of GetAllSubjects: " + request_response.getData());
                    List<String> associated_subjects = (List<String>) request_response.getData();
                    System.out.println("Got associated_subjects to class " + my_class_name_as_string + ": " + associated_subjects);
                    set_all_subjects(associated_subjects);
                }
                else{
                    System.out.println("Error in GetAllSubjects: " + get_subjects_result.getError_message());
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                String err_message = t.getMessage();
                System.out.println("Enqueueing a GetAllSubjects call failed! Failure message: \n" + err_message);
            }
        });
    }

    private void show_missing_subjects(){
        JsonPlaceHolderApi jsonPlaceHolderApi = MainActivity.getRetrofitJsonPlaceHolderApi();
        SubjectRequest edit_class = new SubjectRequest(my_user_id_as_string,
                my_class_name_as_string,
                null,
                null,
                null,
                null,
                null);
        Call<GenericResponse> edit_class_response = jsonPlaceHolderApi.GetMissingSubjects(edit_class);
        edit_class_response.enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                Tuple get_subjects_result = MainActivity.log_request_errors(response, MainActivity.TEACHER, MainActivity.GET_MISSING_SUBJECTS);
                if (get_subjects_result.getSucceeded()){
                    GenericResponse request_response = response.body();
                    System.out.println("Entire response of GetMissingSubjects: " + request_response.getData());
                    List<String> associated_subjects = (List<String>) request_response.getData();
                    System.out.println("Got missing subjects: " + associated_subjects);
                    set_missing_subjects(associated_subjects);
                }
                else{
                    System.out.println("Error in GetMissingSubjects: " + get_subjects_result.getError_message());
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                String err_message = t.getMessage();
                System.out.println("Enqueueing a GetMissingSubjects call failed! Failure message: \n" + err_message);
            }
        });
    }

    private void show_needed_subjects(){
        JsonPlaceHolderApi jsonPlaceHolderApi = MainActivity.getRetrofitJsonPlaceHolderApi();
        SubjectRequest edit_class = new SubjectRequest(my_user_id_as_string,
                my_class_name_as_string,
                null,
                null,
                null,
                null,
                null);
        Call<GenericResponse> edit_class_response = jsonPlaceHolderApi.GetNeededSubjects(edit_class);
        edit_class_response.enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                Tuple get_subjects_result = MainActivity.log_request_errors(response, MainActivity.TEACHER, MainActivity.GET_NEEDED_SUBJECTS);
                if (get_subjects_result.getSucceeded()){
                    GenericResponse request_response = response.body();
                    System.out.println("Entire response of GetNeededSubjects: " + request_response.getData());
                    List<String> associated_subjects = (List<String>) request_response.getData();
                    System.out.println("Got missing subjects: " + associated_subjects);
                    set_needed_subjects(associated_subjects);
                }
                else{
                    System.out.println("Error in GetNeededSubjects: " + get_subjects_result.getError_message());
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                String err_message = t.getMessage();
                System.out.println("Enqueueing a GetMissingSubjects call failed! Failure message: \n" + err_message);
            }
        });
    }

    private void set_all_subjects(List<String> subjects){
        String ordered_subjects = MainActivity.order_list_items_to_txt(subjects);
        String msg = "All subjects related to the class are:\n" + ordered_subjects;
        System.out.println(msg);
        all_subjects_text_view.setText(msg);
    }

    private void set_needed_subjects(List<String> subjects){
        String ordered_subjects = MainActivity.order_list_items_to_txt(subjects);
        String msg = "Needed subjects for tomorrow are:\n" + ordered_subjects;
        System.out.println(msg);
        needed_subjects_text_view.setText(msg);
    }

    private void set_missing_subjects(List<String> subjects){
        String ordered_subjects = MainActivity.order_list_items_to_txt(subjects);
        String msg = "Missing subjects are:\n" + ordered_subjects;
        System.out.println(msg);
        missing_subjects_text_view.setText(msg);
    }
}
