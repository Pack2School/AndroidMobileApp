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
import java.util.List;

public class teacher_main_page extends AppCompatActivity implements DialogBox.ExampleDialogListener{

    Intent myIntent;
    String my_user_id_as_string;
    String my_user_name_as_string;
    TextView user_name_text_view;
    TextView user_id_text_view;

    Button create_new_class_btn;
    Button update_tomorrows_books_btn;
    Button edit_class_btn;
    Spinner selected_class_spinner;
    ArrayAdapter<String> spinner_adapter;
    String selected_class;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_main_page);

        myIntent = getIntent();
        my_user_id_as_string = myIntent.getStringExtra(MainActivity.USER_ID);
        my_user_name_as_string = myIntent.getStringExtra(MainActivity.NAME);
        user_name_text_view = (TextView)findViewById(R.id.user_name_text_view);
        user_id_text_view = (TextView)findViewById(R.id.user_id_text_view);

        List<String> classes_ids = (List<String>) myIntent.getStringArrayListExtra(MainActivity.CLASSES_IDS);

        if (classes_ids == null){
            classes_ids = new ArrayList<>();
        }

        List<String> choices = new ArrayList<>();
        for (String choice: classes_ids){
            choices.add(choice);
        }

        user_name_text_view.setText("Welcome " + my_user_name_as_string);
        user_id_text_view.setText("User ID: " + my_user_id_as_string + ", Teacher.");


        selected_class_spinner = (Spinner) findViewById(R.id.selected_class_spinner);
        spinner_adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, choices);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selected_class_spinner.setAdapter(spinner_adapter);
        selected_class_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
             @Override
             public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                 selected_class = (String) parent.getSelectedItem();
                 displaySelectedClass(selected_class);
             }

             @Override
             public void onNothingSelected(AdapterView<?> parent) {

             }
        });

        create_new_class_btn = (Button) findViewById(R.id.create_class_btn);
        update_tomorrows_books_btn = (Button)findViewById(R.id.update_tomorrow_btn);
        edit_class_btn = (Button)findViewById(R.id.edit_class_btn);

        create_new_class_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_create_new_class_dialog_box();
            }
        });

        update_tomorrows_books_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selected_class == null){
                    return;
                }
                open_update_books_window(my_user_id_as_string, selected_class);
            }
        });

        edit_class_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selected_class == null){
                    return;
                }
                open_edit_class_window(my_user_id_as_string, selected_class);
            }
        });
    }

    private void open_update_books_window(String teacher_id, String class_name){
        // TODO - USE get_class_subjects_and_open_next_intent to avoid code duplication
        JsonPlaceHolderApi jsonPlaceHolderApi = MainActivity.getRetrofitJsonPlaceHolderApi();
        SubjectRequest edit_class = new SubjectRequest(null,
                class_name,
                null,
                null,
                null,
                null,
                null);
        Call<GenericResponse> edit_class_response = jsonPlaceHolderApi.GetAllSubjects(edit_class);
        edit_class_response.enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                Tuple get_subjects_result = MainActivity.log_request_errors(response, MainActivity.TEACHER, MainActivity.EDIT_CLASS);
                if (get_subjects_result.getSucceeded()){
                    GenericResponse request_response = response.body();
                    System.out.println("Entire response of GetAllSubjects: " + request_response.getData());
                    List<String> associated_subjects = (List<String>) request_response.getData();
                    System.out.println("Got associated_subjects to class " + class_name + ": " + associated_subjects);
                    _open_update_books_window(teacher_id, class_name, associated_subjects);
                }
                else{
                    show_message("Error: " + get_subjects_result.getError_message());
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                String err_message = t.getMessage();
                show_message("Error: " + err_message);
                System.out.println("Enqueueing a GetAllSubjects call failed! Failure message: \n" + err_message);
            }
        });
    }

    private void _open_update_books_window(String teacher_id, String class_name,  List<String> subjects){
        ArrayList<String> casted_subjects = new ArrayList<>(subjects.size());
        casted_subjects.addAll(subjects);
        Intent intent = new Intent(this, teacher_update_class_books_page.class);
        intent.putExtra(MainActivity.USER_ID, my_user_id_as_string);
        intent.putExtra(MainActivity.CLASSES_IDS, selected_class);
        intent.putExtra(MainActivity.SUBJECTS, casted_subjects);
        startActivity(intent);
    }

    private void open_edit_class_window(String teacher_id, String class_name){
        // TODO - USE get_class_subjects_and_open_next_intent to avoid code duplication
        JsonPlaceHolderApi jsonPlaceHolderApi = MainActivity.getRetrofitJsonPlaceHolderApi();
        SubjectRequest edit_class = new SubjectRequest(null,
                class_name,
                null,
                null,
                null,
                null,
                null);
        Call<GenericResponse> edit_class_response = jsonPlaceHolderApi.GetAllSubjects(edit_class);
        edit_class_response.enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                Tuple get_subjects_result = MainActivity.log_request_errors(response, MainActivity.TEACHER, MainActivity.EDIT_CLASS);
                if (get_subjects_result.getSucceeded()){
                    GenericResponse request_response = response.body();
                    System.out.println("Entire response of GetAllSubjects: " + request_response.getData());
                    List<String> associated_subjects = (List<String>) request_response.getData();
                    System.out.println("Got associated_subjects to class " + class_name + ": " + associated_subjects);
                    open_edit_class_window_using_main_activity(teacher_id, class_name, associated_subjects);
                }
                else{
                    show_message("Error: " + get_subjects_result.getError_message());
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                String err_message = t.getMessage();
                show_message("Error: " + err_message);
                System.out.println("Enqueueing a GetAllSubjects call failed! Failure message: \n" + err_message);
            }
        });
    }

    private void open_edit_class_window_using_main_activity(String teacher_id, String class_name, List<String> subjects){
        ArrayList<String> casted_subjects = new ArrayList<>(subjects.size());
        casted_subjects.addAll(subjects);
        Intent intent =  MainActivity.open_teacher_edit_class_page(this, teacher_id, class_name, casted_subjects);
        startActivity(intent);
    }

    private void displaySelectedClass(String class_name) {
        String SelectedData = "Selected class: " + class_name;
        Toast.makeText(this, SelectedData, Toast.LENGTH_LONG).show();
    }

    private void show_message(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void open_create_new_class_dialog_box() {
        DialogBox dialogBox = new DialogBox();
        dialogBox.show(getSupportFragmentManager(), "Create a new class");
    }

    @Override
    public void applyTexts(String class_name) {
        JsonPlaceHolderApi jsonPlaceHolderApi = MainActivity.getRetrofitJsonPlaceHolderApi();
        SchoolClassRequest create_class = new SchoolClassRequest(my_user_id_as_string, class_name);
        Call<GenericResponse> create_class_response = jsonPlaceHolderApi.AddNewClass(create_class);
        create_class_response.enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                Tuple create_class_result = MainActivity.log_request_errors(response, MainActivity.TEACHER, MainActivity.ADD_NEW_CLASS);
                if (create_class_result.getSucceeded()){
                    GenericResponse request_response = response.body();
                    String created_class_name = (String) request_response.getData();
                    System.out.println("Created a new class named " + created_class_name);
                    spinner_adapter.add(created_class_name);
                    spinner_adapter.notifyDataSetChanged();
                }
                else{
                    show_message("Error: " + create_class_result.getError_message());
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                String err_message = t.getMessage();
                show_message("Error: " + err_message);
                System.out.println("Enqueueing a sign up call failed! Failure message: \n" + err_message);
            }
        });
    }
}

//    private void get_class_subjects_and_open_next_intent(String teacher_id, String class_name, Method func){
//        // Get the subjects associated with the class  - and call the next function ONLY from within the background http call.
//        // We do it in order to avoid a situation where a flow of a certain activity continues running, thinking it has some value
//        // that it got via an http request - but since the request failed or took a long time - he in fact does not have it:
//        JsonPlaceHolderApi jsonPlaceHolderApi = MainActivity.getRetrofitJsonPlaceHolderApi();
//        SubjectRequest edit_class = new SubjectRequest(null,
//                class_name,
//                null,
//                null,
//                null,
//                null,
//                null);
//        Call<GenericResponse> edit_class_response = jsonPlaceHolderApi.GetAllSubjects(edit_class);
//        edit_class_response.enqueue(new Callback<GenericResponse>() {
//            @Override
//            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
//                Tuple get_subjects_result = MainActivity.log_request_errors(response, MainActivity.TEACHER, MainActivity.EDIT_CLASS);
//                if (get_subjects_result.getSucceeded()){
//                    GenericResponse request_response = response.body();
//                    System.out.println("Entire response of GetAllSubjects: " + request_response);
//                    List<String> associated_subjects = (List<String>) request_response.getData();
//                    System.out.println("Got associated_subjects to class " + class_name + ": " + associated_subjects);
//                    try {
//                        func.invoke();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//                else{
//                    show_message("Error: " + get_subjects_result.getError_message());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<GenericResponse> call, Throwable t) {
//                String err_message = t.getMessage();
//                show_message("Error: " + err_message);
//                System.out.println("Enqueueing a GetAllSubjects call failed! Failure message: \n" + err_message);
//            }
//        });
//    }
