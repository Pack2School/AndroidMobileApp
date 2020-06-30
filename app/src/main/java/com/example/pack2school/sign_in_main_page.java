package com.example.pack2school;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.microsoft.signalr.Action1;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.HubConnectionState;

import java.io.IOException;

public class sign_in_main_page extends AppCompatActivity {

    Button send_input_btn;

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
                // TODO: Now Call the azure function with our user id and password.. get the user
                //  type from the response and open the correct main page.


                // TODO: For now just open a new student main page displaying the entered id:
                //int user_id = Integer.parseInt(id_input.getText().toString());
                String user_id = id_input.getText().toString();
                call_open_student_main_page(user_id);
            }
        });
    }

    public void call_open_student_main_page(String user_id){
        Intent intent =  MainActivity.open_student_main_page(this, user_id);
        startActivity(intent);
    }
}
