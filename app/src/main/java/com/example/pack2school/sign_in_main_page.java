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
                EditText input_text_line = (EditText) findViewById(R.id.input_text_line);
                //String user_id = input_text_line.getText().toString();
                // string or int...
                int user_id = Integer.parseInt(input_text_line.getText().toString());


                call_open_student_main_page(user_id);

                // TODO: Now Call the azure function with our user id!
                // using signalR this will be:
                //MainActivity.hubConnection.send("Method to sign in..", user_id);
                //using http this will be:
//                OkHttpClient client = new OkHttpClient();
//                String url = "url of azure function";
//                Request request = new Request.Builder().url(url).build();
//                client.newCall(request).enqueue(new Callback() {
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//
//                    }
//
//                    @Override
//                    public void onResponse(Call call, Response response) throws IOException {
//                        // if got back that er are a student:
//
//                        main_methods.open_student_main_page(2); // 2 should be replace by something extracted from response
//                    }
//                });
            }
        });
    }

    public void call_open_student_main_page(int user_id){
        Intent intent =  MainActivity.open_student_main_page(this, user_id);
        startActivity(intent);
    }

}

//    @Background
//    public void doPostText(View view) {
//        String postBody = "{\n" +
//                "    \"email\": \"melar@dev.com\",\n" +
//                "    \"password\": \"melardev\"\n" +
//                "}";
//
//        Request request = new Request.Builder()
//                .url("https://reqres.in/api/register")
//                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8")/*MediaType.parse("text/x-markdown; charset=utf-8")*/, postBody))
//                .build();
//
//        Call call = okHttpClient.newCall(request);
//        Response response = null;
//        try {
//            response = call.execute();
//            String responseStr = response.body().string();
//            updateResult(responseStr);
//            //For authentication tutorial
//            String token = null;
//            try {
//                //token = new JSONObject(response.body().string()).getString("token"); IllegalStateException: closed!!
//                token = new JSONObject(responseStr).getString("token");
//                /*Request requestForAuthorizedUsers = new Request.Builder()
//                        .url("toRestrictedUrl")
//                        .addHeader("Authorization", token
//                        //Credentials.basic("thisismy@email.com", "thisismypassword")
//                        )
//                        .build();
//                        */
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//@Background
//public void doPostJson(View view) {
//    String jsonStr = "{\n" +
//            "    \"name\": \"Melardev\",\n" +
//            "    \"job\": \"Student\"\n" +
//            "}";
//
//    RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonStr);
//
//    Request request = new Request.Builder()
//            .url("https://reqres.in/api/users")
//            .post(body)
//            .build();
//
//
//    Response response = null;
//    try {
//        response = okHttpClient.newCall(request).execute();
//        updateResult(response.body().string()); //json2pojo already explained
//    } catch (IOException e) {
//        e.printStackTrace();
//    }
//}
//
//    @Background
//    public void doGetWithParams(View view) {
//        /*new HttpUrl.Builder()
//                .scheme("https")
//                .host("httpbin.org")
//                .addPathSegment("get")*/
//        String url = HttpUrl.parse("https://httpbin.org/get").newBuilder()
//                .addEncodedQueryParameter("author", "Melar Dev") //for GET requests
//                .addQueryParameter("category", "android") //for GET requests
//                .build().toString();
//
//        Request request = new Request.Builder()
//                .url(url)
//                .build();
//
//
//        Response response = null;
//        try {
//            response = okHttpClient.newCall(request).execute();
//            updateResult(response.body().string());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Background
//    public void doFormPostWithParams(View view) {
//
//        HttpUrl httpUrl = new HttpUrl.Builder()
//                .scheme("https")
//                .host("httpbin.org")
//                .addPathSegment("post")
//                .build();
//
//        FormBody form = new FormBody.Builder()
//                .add("email", "thisismy@email.com")
//                .add("password", "andthisismypassword")
//                .build();
//
//
//        try {
//            updateResult(doSyncPost(okHttpClient, httpUrl, form));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }