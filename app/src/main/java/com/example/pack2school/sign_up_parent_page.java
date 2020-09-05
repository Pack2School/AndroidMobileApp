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
import java.util.Arrays;
import java.util.List;

public class sign_up_parent_page extends AppCompatActivity {

    Button lets_go_btn;
    String name_input_str;
    String id_input_str;
    String children_ids_input_str;
    String email_input_str;
    String password_input_str;
    String password_repeat_input_str;
    String type_input_str = MainActivity.PARENT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_parent_page);

        lets_go_btn = (Button)findViewById(R.id.lets_go_btn);
        lets_go_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText name_input = (EditText) findViewById(R.id.name_input);
                EditText id_input = (EditText) findViewById(R.id.id_input);
                EditText password_input = (EditText) findViewById(R.id.password_input);
                EditText children_ids_input = (EditText) findViewById(R.id.child_user_id_input);
                EditText email_input = (EditText) findViewById(R.id.email_input);
                EditText password_repeat_input = (EditText) findViewById(R.id.password_repeat_input);


                name_input_str = name_input.getText().toString();
                id_input_str = id_input.getText().toString();
                children_ids_input_str = children_ids_input.getText().toString();
                password_input_str = password_input.getText().toString();
                password_repeat_input_str = password_repeat_input.getText().toString();
                email_input_str = email_input.getText().toString();
                if(! MainActivity.are_passwords_aligned(password_input_str, password_repeat_input_str)){
                    show_message("Error: repeated password and initial one are not identical.");
                    return;
                }
                List<String> children_ids = Arrays.asList(children_ids_input_str.split(" "));
                log_children_ids(children_ids);
                JsonPlaceHolderApi jsonPlaceHolderApi = MainActivity.getRetrofitJsonPlaceHolderApi();
                UserRequest sign_up_input = new UserRequest(id_input_str,
                                                            name_input_str,
                                                            type_input_str,
                                                            email_input_str,
                                                            password_input_str,
                                                            children_ids,
                                                           null,
                                                           null,
                                                            null);
                Call<GenericResponse> sign_up_call = jsonPlaceHolderApi.signUp(sign_up_input);
                sign_up_call.enqueue(new Callback<GenericResponse>() {
                    @Override
                    public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                        Tuple sign_up_result = MainActivity.log_request_errors(response, type_input_str, MainActivity.SIGN_UP);
                        if (sign_up_result.getSucceeded()){
                            call_open_parent_main_page(id_input_str, name_input_str, children_ids);
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

    private void call_open_parent_main_page(String student_id, String student_name, List<String> children_ids){
        ArrayList<String> casted_children_ids = new ArrayList<>(children_ids.size());
        casted_children_ids.addAll(children_ids);
        Intent intent = MainActivity.open_parent_main_page(this, student_id, student_name, casted_children_ids);
        startActivity(intent);
    }

    private void show_message(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void log_children_ids(List<String> children_ids){
        System.out.println("Parent passed the following children ids: (total of " + children_ids.size() + ")\n");
        for(String child_id: children_ids){
            System.out.println(child_id + "\n");
        }
    }
}