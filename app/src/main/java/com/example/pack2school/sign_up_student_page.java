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

import java.util.List;
import java.util.Map;


public class sign_up_student_page extends AppCompatActivity {

    Button lets_go_btn;
    String name_input_str;
    String id_input_str;
    String class_id_input_str;
    String email_input_str;
    String password_input_str;
    String password_repeat_input_str;
    String type_input_str = MainActivity.STUDENT;
    String teacher_name_input_str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_student_page);

        lets_go_btn = (Button)findViewById(R.id.lets_go_btn);
        lets_go_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText name_input = (EditText) findViewById(R.id.name_input);
                EditText id_input = (EditText) findViewById(R.id.id_input);
                EditText password_input = (EditText) findViewById(R.id.password_input);
                EditText class_id_input = (EditText) findViewById(R.id.class_id_input);
                EditText email_input = (EditText) findViewById(R.id.email_input);
                EditText password_repeat_input = (EditText) findViewById(R.id.password_repeat_input);
                EditText teacher_name_input = (EditText) findViewById(R.id.teacher_name_input);

                name_input_str = name_input.getText().toString();
                id_input_str = id_input.getText().toString();
                class_id_input_str = class_id_input.getText().toString();
                password_input_str = password_input.getText().toString();
                password_repeat_input_str = password_repeat_input.getText().toString();
                email_input_str = email_input.getText().toString();
                teacher_name_input_str = teacher_name_input.getText().toString();

                if(! MainActivity.are_passwords_aligned(password_input_str, password_repeat_input_str)){
                    show_message("Error: repeated password and initial one are not identical.");
                    return;
                }
                JsonPlaceHolderApi jsonPlaceHolderApi = MainActivity.getRetrofitJsonPlaceHolderApi();
                UserRequest sign_up_input = new UserRequest(id_input_str,
                                                            name_input_str,
                                                            type_input_str,
                                                            email_input_str,
                                                            password_input_str,
                                                            null,
                                                            null,
                                                            class_id_input_str,
                                                            teacher_name_input_str);
                Call<GenericResponse> sign_up_call = jsonPlaceHolderApi.signUp(sign_up_input);
                sign_up_call.enqueue(new Callback<GenericResponse>() {
                    @Override
                    public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                        Tuple sign_up_result = MainActivity.log_request_errors(response, type_input_str, MainActivity.SIGN_UP);
                        if (sign_up_result.getSucceeded()){
                            System.out.println("Entered a successful Student sign up.");
                            GenericResponse sign_up_response = response.body();
                            Map<String, Object> response_data = (Map<String, Object>) sign_up_response.getData();
                            System.out.println("Entered a successful Student sign up - extracted Map<String, Object>.");
                            String device_connection_string = (String) response_data.get(MainActivity.DEVICE_CONNECTION_STRING);
                            System.out.println("Student received following device connection string: \n" + device_connection_string);
                            List<String> user_classes = (List<String>) response_data.get(MainActivity.INFO);
                            MainActivity.log_list_items(user_classes, type_input_str);
                            String user_class = user_classes.get(0);
                            call_open_student_main_page(id_input_str, name_input_str, device_connection_string, user_class);
                        }
                        else{
                            show_message("Error: " + sign_up_result.getError_message());
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

    private void call_open_student_main_page(String student_id, String student_name, String device_connection_string, String class_name){
        Intent intent =  MainActivity.open_student_main_page(this, student_id, student_name, device_connection_string, MainActivity.SIGN_UP, class_name);
        startActivity(intent);
    }

    private void show_message(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
