package com.example.pack2school;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;

public class sign_up_student_page extends AppCompatActivity {

    Button lets_go_btn;

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

                String name_input_str = name_input.getText().toString();
                String id_input_str = id_input.getText().toString();
                String class_id_input_str = class_id_input.getText().toString();
                String password_input_str = password_input.getText().toString();

                // TODO: call the correct azure function with this params...
                HashMap<String, String> params_dict = new HashMap<String, String>();
                params_dict.put(MainActivity.USER_TYPE, "student");
                params_dict.put(MainActivity.NAME, name_input_str);
                params_dict.put(MainActivity.USER_ID, id_input_str);
                params_dict.put(MainActivity.PASSWORD, password_input_str);
                params_dict.put(MainActivity.CLASS_ID, class_id_input_str);
                MainActivity.post_async_http_request("url", params_dict); // TODO: not tested
            }
        });
    }
}
