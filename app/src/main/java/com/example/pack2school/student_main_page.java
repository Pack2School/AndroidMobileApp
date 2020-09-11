package com.example.pack2school;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.signalr.Action1;
import com.microsoft.signalr.HubConnection;

import java.util.ArrayList;
import java.util.List;


public class student_main_page extends AppCompatActivity implements SetStickerDialogBox.ExampleDialogListener{

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
    TextView extra_subjects_text_view;
    Button scan_my_bag_btn;
    Button set_sticker_btn;
    HubConnection hubConnection;
    List<String> all_subjects;
    List<String> needed_subjects;
    List<String> missing_subjects;
    List<String> extra_subjects;
    Spinner selected_subject_spinner;
    ArrayAdapter<String> spinner_adapter;
    String selected_subject;

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
        extra_subjects_text_view = (TextView)findViewById(R.id.extra_subjects_text_view);

        welcome_text_view = (TextView)findViewById(R.id.welcome_text_view);
        if(entrance_type.equals(MainActivity.SIGN_UP)){
            welcome_text_view.setText("Welcome " + my_name_as_string + ". \nPlease follow the instructions you got in school and set the above string to your scanner:\n" + device_connection_string);
        }
        else if(entrance_type.equals(MainActivity.PARENT_CHILD_CHECKUP)){
            welcome_text_view.setText("Status page for student " + my_user_id_as_string);
        }
        else{ // entrance_type == MainActivity.SIGN_IN
            welcome_text_view.setText("Welcome " + my_name_as_string);
        }

        scan_my_bag_btn = (Button)findViewById(R.id.scan_my_bag_btn);
        set_sticker_btn = (Button)findViewById(R.id.set_sticker_btn);

        // Get latest data from cloud and show it to user
        show_all_subjects();
        show_needed_subjects();
        show_missing_subjects();

        // Set spinner for stickers setup:
        selected_subject_spinner = (Spinner) findViewById(R.id.selected_subject_spinner);
        List<String> choices = new ArrayList<>();
        if (all_subjects != null){
            for (String choice: all_subjects){
                choices.add(choice);
            }
        }
        spinner_adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, choices);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selected_subject_spinner.setAdapter(spinner_adapter);
        selected_subject_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_subject = (String) parent.getSelectedItem();
                displaySelectedSubject(selected_subject);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        connect_to_signalR_and_wait_for_updates();

        scan_my_bag_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.call_backpack_scan_op(my_user_id_as_string, MainActivity.STUDENT);
            }
        });

        set_sticker_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (all_subjects == null || all_subjects.size() == 0){ // someone pressed the button before the first http/signalR returned or really nothing there
                    show_message("There are currently no subjects associated with you class, so you can't define stickers yet :(");
                    return;
                }
                open_set_sticker_dialog_box();
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
                hubConnection.on(MainActivity.DataBaseAndScanUpdates, new Action1<DataBaseAndScanUpdates>() {
                    @Override
                    public void invoke(DataBaseAndScanUpdates param1) {
                        System.out.println(MainActivity.DataBaseAndScanUpdates + " was invoked!");
                        System.out.println("Input from method: " + param1);
                        List<String> recv_all = param1.getAllSubjects();
                        needed_subjects = param1.getNeededSubjects();
                        missing_subjects = param1.getMissingSubjects();
                        extra_subjects = param1.getExtraSubjects();
                        String err_msg = param1.getErrorMessage();
                        System.out.println("all: " + recv_all + "\nmissing: " + missing_subjects + "\nneeded: " + needed_subjects + "\nextra: " + extra_subjects + "\nerr: " + err_msg);
                        if(err_msg != null){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    show_message(err_msg);
                                }
                            });
                        }
                        else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(recv_all != null){
                                        all_subjects = recv_all;
                                        set_all_subjects(all_subjects);
                                    }
                                    if(needed_subjects != null){
                                        set_needed_subjects(needed_subjects);
                                    }
                                    if(missing_subjects != null){
                                        set_missing_subjects(missing_subjects,1);
                                    }
                                    if(extra_subjects != null){
                                        set_extra_subjects(extra_subjects);
                                    }
                                }
                            });
                        }
                    }
                }, DataBaseAndScanUpdates.class);
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
                Tuple get_subjects_result = MainActivity.log_request_errors(response, MainActivity.STUDENT, MainActivity.GET_ALL_SUBJECTS);
                if (get_subjects_result.getSucceeded()){
                    GenericResponse request_response = response.body();
                    System.out.println("Entire response of GetAllSubjects: " + request_response.getData());
                    all_subjects = (List<String>) request_response.getData();
                    System.out.println("Got associated_subjects to class " + my_class_name_as_string + ": " + all_subjects);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            set_all_subjects(all_subjects);
                        }
                    });
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
                    missing_subjects = (List<String>) request_response.getData();
                    System.out.println("Got missing subjects: " + missing_subjects);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            set_missing_subjects(missing_subjects,0);
                        }
                    });
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
                    needed_subjects = (List<String>) request_response.getData();
                    System.out.println("Got missing subjects: " + needed_subjects);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            set_needed_subjects(needed_subjects);
                        }
                    });
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
        spinner_adapter.clear();
        for (String item: subjects){
            spinner_adapter.add(item);
        }
        spinner_adapter.notifyDataSetChanged();
    }

    private void set_extra_subjects(List<String> subjects){
        String ordered_subjects = MainActivity.order_list_items_to_txt(subjects);
        String msg = "Extra books in the backpack are:\n" + ordered_subjects;
        System.out.println(msg);
        extra_subjects_text_view.setText(msg);
    }

    private void set_needed_subjects(List<String> subjects){
        String ordered_subjects = MainActivity.order_list_items_to_txt(subjects);
        String msg = "Needed subjects for tomorrow are:\n" + ordered_subjects;
        System.out.println(msg);
        needed_subjects_text_view.setText(msg);
    }

    private void set_missing_subjects(List<String> subjects, Integer update_type){
        String msg = "";
        String ordered_subjects = MainActivity.order_list_items_to_txt(subjects);
        if (update_type == 1){
            msg = "Missing subjects after scan are:\n" + ordered_subjects;
        } else{
            msg = "Missing subjects are:\n" + ordered_subjects;
        }
        System.out.println(msg);
        missing_subjects_text_view.setText(msg);
    }

    private void displaySelectedSubject(String subject_name) {
        String SelectedData = "Selected subject: " + subject_name;
        show_message(SelectedData);
    }

    private void show_message(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void open_set_sticker_dialog_box() {
        SetStickerDialogBox dialogBox = new SetStickerDialogBox();
        dialogBox.show(getSupportFragmentManager(), "Set sticker");
    }

    @Override
    public void applyTexts(String sticker_id) {
        JsonPlaceHolderApi jsonPlaceHolderApi = MainActivity.getRetrofitJsonPlaceHolderApi();
        SubjectRequest edit_class = new SubjectRequest(my_user_id_as_string,
                my_class_name_as_string,
                selected_subject,
                null,
                null,
                null,
                sticker_id);
        Call<GenericResponse> edit_class_response = jsonPlaceHolderApi.UpdateSubjectStickers(edit_class);
        edit_class_response.enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                Tuple update_subjects_result = MainActivity.log_request_errors(response, MainActivity.STUDENT, MainActivity.SET_STICKER);
                if (update_subjects_result.getSucceeded()){
                    System.out.println("Successfully ran UpdateSubjectStickers");
                    show_message("Successfully updated sticker!");
                }
                else{
                    String err_message = update_subjects_result.getError_message();
                    System.out.println("Error in UpdateSubjectStickers: " + err_message);
                    show_message("There was a problem updating the sticker :(\nError is: " + err_message);
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                String err_message = t.getMessage();
                System.out.println("Enqueueing a GetMissingSubjects call failed! Failure message: \n" + err_message);
                show_message("There was a problem updating the sticker :(\nError is: " + err_message);
            }
        });
    }
}
