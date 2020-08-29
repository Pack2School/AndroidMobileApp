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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class parent_main_page extends AppCompatActivity {

    Intent myIntent;
    String my_user_id_as_string;
    String my_user_name_as_string;
    TextView user_name_text_view;
    TextView user_id_text_view;
    TextView needed_books_text_view;
    TextView missing_books_text_view;
    Button scan_backpack_btn;
    Spinner choose_child_spinner;
    String selected_child;
    String selected_child_class_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_main_page);

        myIntent = getIntent();
        my_user_id_as_string = myIntent.getStringExtra(MainActivity.USER_ID);
        my_user_name_as_string = myIntent.getStringExtra(MainActivity.NAME);
        user_name_text_view = (TextView)findViewById(R.id.user_name_text_view);
        user_id_text_view = (TextView)findViewById(R.id.user_id_text_view);
        needed_books_text_view = (TextView)findViewById(R.id.needed_books_text_view);
        missing_books_text_view = (TextView)findViewById(R.id.missing_books_text_view);

        user_name_text_view.setText("Welcome " + my_user_name_as_string);
        user_id_text_view.setText("User ID: " + my_user_id_as_string + ", Parent.");

        List<String> children_ids = (List<String>) myIntent.getStringArrayListExtra(MainActivity.CHILDREN_IDS);

        if (children_ids == null){
            children_ids = new ArrayList<>();
            children_ids.add(MainActivity.NO_CHILDREN_RECEIVED);
        }
        List<String> choices = new ArrayList<>();
        for (String choice: children_ids){
            choices.add(choice);
        }
        // Set up the spinner:
        choose_child_spinner = (Spinner) findViewById(R.id.selected_child_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, choices);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        choose_child_spinner.setAdapter(adapter);
        choose_child_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_child = (String) parent.getSelectedItem();
                displaySelectedChild(selected_child);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Set up the btn to send the correct http:
        scan_backpack_btn = (Button) findViewById(R.id.scan_my_bag_btn);
        scan_backpack_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selected_child.equals(MainActivity.NO_CHILDREN_RECEIVED)){
                    return;
                }
                List<String> list_of_requested_child = new ArrayList<>();
                list_of_requested_child.add(selected_child);
                JsonPlaceHolderApi jsonPlaceHolderApi = MainActivity.getRetrofitJsonPlaceHolderApi();

                UserRequest request_for_child_class_identifier = new UserRequest(null,
                        null,
                        null,
                        null,
                        null,
                        list_of_requested_child,
                        null,
                        null,
                        null);
                Call<GenericResponse> class_id_request = jsonPlaceHolderApi.GetStudentClassTableName(request_for_child_class_identifier);
                class_id_request.enqueue(new Callback<GenericResponse>() {
                    @Override
                    public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                        Tuple sign_up_result = MainActivity.log_request_errors(response, MainActivity.PARENT, MainActivity.GET_STUDENT_CLASS_ID);
                        if (sign_up_result.getSucceeded()){
                            System.out.println("Parent successfully got child's class ID. Parsing..");
                            GenericResponse response_body = response.body();
                            List<String> response_data = (List<String>) response_body.getData(); // should be a list of size 1. If it's not - we should have received an error.
                            selected_child_class_id = response_data.get(0);
                            System.out.println("Parent successfully got child's class ID: " + selected_child_class_id);
                        }
                        else{
                            show_message("Error: " + sign_up_result.getError_message());
                        }
                    }

                    @Override
                    public void onFailure(Call<GenericResponse> call, Throwable t) {
                        String internal_err_message = "Enqueueing a GetStudentClassTableName call failed! Failure message: \n" + t.getMessage();
                        String err_message = "Error: " + t.getMessage();
                        log_message(err_message, internal_err_message);
                    }
                });

                // Now you have selected_child_class_id - and you can ask to know this child's backpack status..
                SubjectRequest request_for_student_subjects = new SubjectRequest(selected_child,
                        selected_child_class_id,
                        null,
                        null,
                        null,
                        null,
                        null);

                Call<GenericResponse> needed_subjects_request = jsonPlaceHolderApi.GetNeededSubjects(request_for_student_subjects);
                needed_subjects_request.enqueue(new Callback<GenericResponse>() {
                    @Override
                    public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                        Tuple call_result = MainActivity.log_request_errors(response, MainActivity.PARENT, MainActivity.GET_NEEDED_SUBJECTS);
                        if (call_result.getSucceeded()){
                            GenericResponse response_body = response.body();
                            List<String> response_data = (List<String>) response_body.getData();
                            needed_books_text_view.setText("Needed books are: \n" + response_data);
                        }
                        else{
                            show_message("Error: " + call_result.getError_message());
                        }
                    }

                    @Override
                    public void onFailure(Call<GenericResponse> call, Throwable t) {
                        String internal_err_message = "Enqueueing a GetNeededSubjects call failed! Failure message: \n" + t.getMessage();
                        String err_message = "Error: " + t.getMessage();
                        log_message(err_message, internal_err_message);
                    }
                });

                Call<GenericResponse> scan_request = jsonPlaceHolderApi.GetMissingSubjects(request_for_student_subjects);
                scan_request.enqueue(new Callback<GenericResponse>() {
                    @Override
                    public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                        Tuple call_result = MainActivity.log_request_errors(response, MainActivity.PARENT, MainActivity.GET_MISSING_SUBJECTS);
                        if (call_result.getSucceeded()){
                            GenericResponse response_body = response.body();
                            List<String> response_data = (List<String>) response_body.getData();
                            missing_books_text_view.setText("Missing books are: \n" + response_data);
                        }
                        else{
                            show_message("Error: " + call_result.getError_message());
                        }
                    }

                    @Override
                    public void onFailure(Call<GenericResponse> call, Throwable t) {
                        String internal_err_message = "Enqueueing a GetMissingSubjects call failed! Failure message: \n" + t.getMessage();
                        String err_message = "Error: " + t.getMessage();
                        log_message(err_message, internal_err_message);
                    }
                });
            }
        });
    }

    private void displaySelectedChild(String child_name) {
        String SelectedData = "Selected child: " + child_name;
        Toast.makeText(this, SelectedData, Toast.LENGTH_LONG).show();
    }

    private void log_message(String err_message, String internal_err_message){
        show_message(err_message);
        System.out.println(internal_err_message);
    }

    private void show_message(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
