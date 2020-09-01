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
import java.util.Map;


public class sign_in_main_page extends AppCompatActivity {

    Button send_input_btn;
    Intent next_intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_main_page);
        send_input_btn = (Button)findViewById(R.id.send_input_btn);
        send_input_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText id_input = (EditText) findViewById(R.id.id_input);
                EditText password_input = (EditText) findViewById(R.id.password_input);
                String id_input_str = id_input.getText().toString();
                String password_input_str = password_input.getText().toString();
                JsonPlaceHolderApi jsonPlaceHolderApi = MainActivity.getRetrofitJsonPlaceHolderApi();
                UserRequest sign_in_input = new UserRequest(id_input_str,
                        null,
                        null,
                        null,
                        password_input_str,
                        null,
                        null,
                        null,
                        null);
                Call<GenericResponse> sign_in_call = jsonPlaceHolderApi.signIn(sign_in_input);
                sign_in_call.enqueue(new Callback<GenericResponse>() {
                    @Override
                    public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                        if (!response.isSuccessful()) {
                            System.out.println("Error occurred in sign in. Error code: " + response.code() + "Error message:" + response.message());
                            show_message("Error: " + response.message());
                            return;
                        }
                        GenericResponse sign_in_response = response.body();
                        String error_message = sign_in_response.getErrorMessage();
                        boolean is_request_successful = sign_in_response.didRequestSucceed();
                        if (is_request_successful){
                            System.out.println("sign in was successful.");
                            // The return of a sign in is a dictionary containing both the user type
                            // and the list of classes associated with that user:
                            Map<String, Object> response_data = (Map<String, Object>) sign_in_response.getData();
                            System.out.println("Sign in entire data response object: " + response_data);
                            String user_type = (String) response_data.get(MainActivity.USER_TYPE);
                            String user_name = (String) response_data.get(MainActivity.NAME);

                            if(user_type.equals(MainActivity.STUDENT)){
                                List<String> user_classes = (List<String>) response_data.get(MainActivity.INFO);
                                MainActivity.log_list_items(user_classes, user_type);
                                String user_class = user_classes.get(0);
                                next_intent = get_open_student_main_page_intent(id_input_str, user_name, user_class);
                            }
                            else if(user_type.equals(MainActivity.TEACHER)){
                                List<String> user_classes = (List<String>) response_data.get(MainActivity.INFO);
                                MainActivity.log_list_items(user_classes, user_type);
                                next_intent = get_open_teacher_main_page_intent(id_input_str, user_name, user_classes);
                            }
                            else if(user_type.equals(MainActivity.PARENT)){
                                List<String> childrenIDs = (List<String>) response_data.get(MainActivity.INFO);
                                MainActivity.log_list_items(childrenIDs, user_type);
                                next_intent = get_open_parent_main_page_intent(id_input_str, user_name, childrenIDs);
                            }
                            else{
                                error_message = "Got back un-familiar user type " + user_type + " \n";
                                System.out.println("Sign in failed: " + error_message);
                                show_message("Error: " + error_message);
                                return;
                            }
                            startActivity(next_intent);
                        } else{
                            System.out.println("sign in was not successful. Error is: " + error_message);
                            show_message("Error: " + error_message);
                        }
                    }

                    @Override
                    public void onFailure(Call<GenericResponse> call, Throwable t) {
                        String err_message = t.getMessage();
                        show_message("Error: " + err_message);
                        System.out.println("Enqueueing a sign in call failed! Failure message: \n" + err_message);
                    }
                });
            }
        });
    }

    private void show_message(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


    private Intent get_open_student_main_page_intent(String user_id, String user_name, String class_name){
        Intent intent =  MainActivity.open_student_main_page(this, user_id, user_name, null, MainActivity.SIGN_IN, class_name);
        return intent;
    }

    private Intent get_open_parent_main_page_intent(String user_id, String user_name, List<String> childrenIDs){
        ArrayList<String> casted_children_ids = new ArrayList<>(childrenIDs.size());
        casted_children_ids.addAll(childrenIDs);
        Intent intent =  MainActivity.open_parent_main_page(this, user_id, user_name, casted_children_ids);
        return intent;
    }

    private Intent get_open_teacher_main_page_intent(String user_id, String user_name, List<String> classesIDs){
        ArrayList<String> casted_classes_ids = new ArrayList<>(classesIDs.size());
        casted_classes_ids.addAll(classesIDs);
        Intent intent =  MainActivity.open_teacher_main_page(this, user_id, user_name, casted_classes_ids);
        return intent;
    }
}
