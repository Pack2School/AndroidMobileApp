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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class teacher_edit_class_page extends AppCompatActivity {

    Intent myIntent;
    String my_user_id_as_string;  // not used now - but perhaps will be needed in the future
    String my_class_name_as_string;
    TextView class_name_text_view;
    EditText new_subject_edit_text;
    String new_subject_edit_text_str;
    Spinner selected_subject_spinner;
    ArrayAdapter<String> spinner_adapter;
    Button add_subject_btn;
    Button rename_subject_btn;
    Button delete_subject_btn;
    String selected_subject_name_str;
    List<String> spinner_choices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_edit_class_page);

        myIntent = getIntent();
        my_user_id_as_string = myIntent.getStringExtra(MainActivity.USER_ID);
        my_class_name_as_string = myIntent.getStringExtra(MainActivity.CLASSES_IDS);
        class_name_text_view = (TextView)findViewById(R.id.class_name_text_view);
        class_name_text_view.setText("Edit Class " + my_class_name_as_string);
        new_subject_edit_text = (EditText) findViewById(R.id.new_subject_edit_text);

        // Get the subjects of the class:
        List<String> subjects = (List<String>) myIntent.getStringArrayListExtra(MainActivity.SUBJECTS);
        if (subjects == null){
            subjects = new ArrayList<>();
        }

        spinner_choices = new ArrayList<>();
        for (String choice: subjects){
            spinner_choices.add(choice);
        }

        // Now present the subjects you got in the spinner:
        selected_subject_spinner = (Spinner) findViewById(R.id.selected_subject_spinner);
        present_spinner(); // uses the class public spinner_choices and selected_subject_spinner

        // Set button listeners to each operation:
        add_subject_btn = (Button) findViewById(R.id.add_subject_btn);
        add_subject_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new_subject_edit_text_str = new_subject_edit_text.getText().toString();
                SubjectRequest add_req = get_subject_request_for_edit_operation(my_class_name_as_string, new_subject_edit_text_str, null, MainActivity.ADD_SUBJECT);
                enqueue_an_edit_subject_operation(add_req);
            }
        });

        rename_subject_btn = (Button) findViewById(R.id.rename_subject_btn);
        rename_subject_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selected_subject_name_str.equals(MainActivity.NO_CLASSES_RECEIVED)){
                    return;
                }
                new_subject_edit_text_str = new_subject_edit_text.getText().toString();
                SubjectRequest rename_req = get_subject_request_for_edit_operation(my_class_name_as_string, selected_subject_name_str, new_subject_edit_text_str, MainActivity.RENAME_SUBJECT);
                enqueue_an_edit_subject_operation(rename_req);
            }
        });

        delete_subject_btn = (Button) findViewById(R.id.delete_subject_btn);
        delete_subject_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selected_subject_name_str.equals(MainActivity.NO_CLASSES_RECEIVED)){
                    return;
                }
                SubjectRequest delete_req = get_subject_request_for_edit_operation(my_class_name_as_string, selected_subject_name_str, null, MainActivity.DELETE_SUBJECT);
                enqueue_an_edit_subject_operation(delete_req);
            }
        });
    }

    private SubjectRequest get_subject_request_for_edit_operation(String tableName, String subject_name, String new_name, String request_type){
        SubjectRequest subject_request = new SubjectRequest(null,
                tableName,
                subject_name,
                new_name,
                request_type,
                null,
                null);
        return subject_request;
    }


    private void enqueue_an_edit_subject_operation(SubjectRequest subject_request){
        JsonPlaceHolderApi jsonPlaceHolderApi = MainActivity.getRetrofitJsonPlaceHolderApi();
        Call<GenericResponse> edit_class_response = jsonPlaceHolderApi.EditSubject(subject_request);
        edit_class_response.enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                Tuple get_subjects_result = MainActivity.log_request_errors(response, MainActivity.TEACHER, subject_request.getRequestType());
                if (get_subjects_result.getSucceeded()){
                    GenericResponse request_response = response.body();
                    System.out.println("Entire response of EditSubject: " + request_response.getData());
                    if (subject_request.getRequestType().equals(MainActivity.ADD_SUBJECT)){
                        spinner_adapter.add(subject_request.getSubjectName());
                        spinner_adapter.notifyDataSetChanged();
                    }
                    else if(subject_request.getRequestType().equals(MainActivity.DELETE_SUBJECT)){
                        spinner_adapter.remove(subject_request.getSubjectName());
                        spinner_adapter.notifyDataSetChanged();
                    }
                    else if(subject_request.getRequestType().equals(MainActivity.RENAME_SUBJECT)){
                        spinner_adapter.remove(subject_request.getSubjectName());
                        spinner_adapter.add(subject_request.getNewSubjectName());
                        spinner_adapter.notifyDataSetChanged();
                    }
                }
                else{
                    show_message("Error: " + get_subjects_result.getError_message());
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                String err_message = t.getMessage();
                show_message("Error: " + err_message);
                System.out.println("Enqueueing a EditSubject call failed! Failure message: \n" + err_message);
            }
        });
    }

    private void present_spinner(){
        spinner_adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, spinner_choices);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selected_subject_spinner.setAdapter(spinner_adapter);
        selected_subject_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_subject_name_str = (String) parent.getSelectedItem();
                displaySelectedSubject(selected_subject_name_str);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void displaySelectedSubject(String subject_name) {
        String SelectedData = "Selected subject: " + subject_name;
        Toast.makeText(this, SelectedData, Toast.LENGTH_LONG).show();
    }

    private void show_message(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}