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
                EditText school_input = (EditText) findViewById(R.id.school_input);
                EditText email_input = (EditText) findViewById(R.id.email_input);
                EditText class_id_input = (EditText) findViewById(R.id.class_id_input);

                String name_input_str = name_input.getText().toString();
                String school_input_str = school_input.getText().toString();
                String email_input_str = email_input.getText().toString();
                String class_id_input_str = class_id_input.getText().toString();

                // TODO: call the correct azure function with this params...
                HashMap<String, String> params_dict = new HashMap<String, String>();
                params_dict.put(MainActivity.USER_TYPE, "student");
                params_dict.put(MainActivity.NAME, name_input_str);
                params_dict.put(MainActivity.SCHOOL, school_input_str);
                params_dict.put(MainActivity.EMAIL, email_input_str);
                params_dict.put(MainActivity.CLASS_ID, class_id_input_str);
                MainActivity.post_async_http_request("url", params_dict);

//                // With http 3:
//                okhttp3.Request request = new okhttp3.Request.Builder().url(url).post(body).build();
//
//                OkHttpClient client = new OkHttpClient();
//                RequestBody requestBody = new MultipartBody.Builder()
//                        .setType(MultipartBody.FORM)
//                        .addFormDataPart("somParam", "someValue")
//                        .build();
//
//                Request request = new Request.Builder()
//                        .url(BASE_URL + route)
//                        .post(requestBody)
//                        .build();
//                try {
//                    Response response = client.newCall(request).execute();
//
//                    // Do something with the response.
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

//                // with creating uri:
//                URL url = null;
//                try {
//                    url = new URL("<server url>");
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                List<NameValuePair> listOfParameters = ...;
//                URI uri = new URIBuilder("http://example.com:8080/path/to/resource?mandatoryParam=someValue")
//                        .addParameter("firstParam", firstVal)
//                        .addParameter("secondParam", secondVal)
//                        .addParameters(listOfParameters)
//                        .build();
//                //Otherwise, you can specify all parameters explicitly:
//
//                URI uri = new URIBuilder()
//                        .setScheme("http")
//                        .setHost("example.com")
//                        .setPort(8080)
//                        .setPath("/path/to/resource")
//                        .addParameter("mandatoryParam", "someValue")
//                        .addParameter("firstParam", firstVal)
//                        .addParameter("secondParam", secondVal)
//                        .addParameters(listOfParameters)
//                        .build();
            }
        });
    }
}
