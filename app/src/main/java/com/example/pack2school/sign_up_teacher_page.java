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


public class sign_up_teacher_page extends AppCompatActivity {

    Button lets_go_btn;
    String name_input_str;
    String id_input_str;
    String email_input_str;
    String password_input_str;
    String password_repeat_input_str;
    String type_input_str = MainActivity.TEACHER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_teacher_page);

        lets_go_btn = (Button)findViewById(R.id.lets_go_btn);
        lets_go_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText name_input = (EditText) findViewById(R.id.name_input);
                EditText id_input = (EditText) findViewById(R.id.id_input);
                EditText email_input = (EditText) findViewById(R.id.email_input);
                EditText password_input = (EditText) findViewById(R.id.password_input);
                EditText password_repeat_input = (EditText) findViewById(R.id.password_repeat_input);

                name_input_str = name_input.getText().toString();
                id_input_str = id_input.getText().toString();
                email_input_str = email_input.getText().toString();
                password_input_str = password_input.getText().toString();
                password_repeat_input_str = password_repeat_input.getText().toString();
                Tuple sign_up_items = MainActivity.check_signup_items(name_input_str, id_input_str, password_input_str, password_repeat_input_str);
                if(!sign_up_items.getSucceeded()){
                    show_message(sign_up_items.getError_message());
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
                                                            null,
                                                            null);
                Call<GenericResponse> sign_up_call = jsonPlaceHolderApi.signUp(sign_up_input);
                sign_up_call.enqueue(new Callback<GenericResponse>() {
                    @Override
                    public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                        Tuple sign_up_result = MainActivity.log_request_errors(response, type_input_str, MainActivity.SIGN_UP);
                        if (sign_up_result.getSucceeded()){
                            GenericResponse request_response = response.body();
                            System.out.println("Entire response of teacher sign up: " + request_response.getData());
                            String given_user_name = (String) request_response.getData();
                            call_open_teacher_main_page(id_input_str, given_user_name);
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

    private void call_open_teacher_main_page(String teacher_id, String teacher_name){
        Intent intent =  MainActivity.open_teacher_main_page(this, teacher_id, teacher_name, null);
        startActivity(intent);
    }

    private void show_message(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
