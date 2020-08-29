package com.example.pack2school;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class teacher_create_class_page extends AppCompatActivity {

    Intent myIntent;
    Button send_input_btn;
    String class_name_str;
    String teacher_id_as_string;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_create_class_page);
        myIntent = getIntent();
        teacher_id_as_string = myIntent.getStringExtra(MainActivity.USER_ID);

        EditText class_id_input = (EditText) findViewById(R.id.class_name_edit_text);

        send_input_btn = (Button)findViewById(R.id.send_input_btn);
        send_input_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                class_name_str = class_id_input.getText().toString();
                JsonPlaceHolderApi jsonPlaceHolderApi = MainActivity.getRetrofitJsonPlaceHolderApi();
                SchoolClassRequest create_class = new SchoolClassRequest(teacher_id_as_string, class_name_str);
                Call<GenericResponse> create_class_response = jsonPlaceHolderApi.AddNewClass(create_class);
                create_class_response.enqueue(new Callback<GenericResponse>() {
                    @Override
                    public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                        Tuple create_class_result = MainActivity.log_request_errors(response, MainActivity.TEACHER, MainActivity.ADD_NEW_CLASS);
                        if (create_class_result.getSucceeded()){
                            GenericResponse request_response = response.body();
                            String created_class_name = (String) request_response.getData();
                            System.out.println("Created a new class named " + created_class_name);
                            open_edit_class_window(teacher_id_as_string, created_class_name);
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
        });
    }

    private void show_message(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void open_edit_class_window(String teacher_id, String class_name){
        Intent intent =  MainActivity.open_teacher_edit_class_page(this, teacher_id, class_name, null);
        startActivity(intent);
    }
}