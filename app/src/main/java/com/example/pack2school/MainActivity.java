package com.example.pack2school;

import androidx.appcompat.app.AppCompatActivity;
import cz.msebera.android.httpclient.Header;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.microsoft.signalr.Action1;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.HubConnectionState;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.loopj.android.http.*;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    public static final String USER_TYPE = "USER_TYPE";
    public static final String USER_ID = "USER_ID";
    public static final String NAME = "NAME";
    public static final String SCHOOL = "SCHOOL";
    public static final String EMAIL = "EMAIL";
    public static final String CLASS_ID = "CLASS_ID";
    public static final String CHILDREN_IDS = "CHILDREN_IDS";

    Button sign_up_btn;
    Button sign_in_btn;

    //Create a hubConnection:
    public static HubConnection hubConnection = HubConnectionBuilder.create("our signalR negotiate path").build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Our buttons that we added in the activity_main.xml:
        sign_up_btn = (Button)findViewById(R.id.Sign_up);
        sign_in_btn = (Button)findViewById(R.id.Sign_in);

        sign_up_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_sign_up_page();
            }
        });

        sign_in_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_sign_in_page();
            }
        });
    }

    public void open_sign_up_page(){
        Intent intent = new Intent(this, sign_up_main_page.class);
        startActivity(intent);
    }

    public void open_sign_in_page(){
        Intent intent = new Intent(this, sign_in_main_page.class);
        startActivity(intent);
    }

    // When opening a page for specific user (will be called from both the sign_in and sign_up pages
    // we will wand to pass the user id (which we will extract upon opening the page, in order
    // to right away call some other function with the user_id and show the corresponding page).
    // TODO - we may change the user_id input to something else, or just add extra params, it all
    //  depends on what exactly the azure functions will return.

    public static Intent open_student_main_page(Context ctx, int user_id){
        Intent intent = new Intent(ctx, student_main_page.class);
        intent.putExtra("user_id", user_id);
        return intent;
    }

    public static Intent open_teacher_main_page(Context ctx, int user_id){
        Intent intent = new Intent(ctx, teacher_main_page.class);
        intent.putExtra("user_id", user_id);
        return intent;
    }

    public static Intent open_parent_main_page(Context ctx, int user_id){
        Intent intent = new Intent(ctx, parent_main_page.class);
        intent.putExtra("user_id", user_id);
        return intent;
    }

//    public static void send_http_request(String url, Dictionary params_dict){
//        // First create the connection:
//        try {
//            URL url_obj = new URL(url);
//            HttpURLConnection con = (HttpURLConnection) url_obj.openConnection();
//            con.setRequestMethod("POST");
//            con.setRequestProperty("User-Agent", "Mozilla/5.0");
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//    }

    public static void get_async_http_request(String url, HashMap<String, String> params_dict){
        new AsyncHttpClient().get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    public static void post_async_http_request(String url, HashMap<String, String> params_dict){
        RequestParams our_params = new RequestParams();
        for (String key : params_dict.keySet()){
            our_params.add(key, params_dict.get(key));
        }
        new AsyncHttpClient().post(url, our_params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    public static void yet_another_http_try(String url_str, HashMap<String, String> params_dict) throws Exception{
        URL url = new URL(url_str);
        StringBuilder postData = new StringBuilder();
        for (Map.Entry param : params_dict.entrySet()){
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode((String) param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        conn.setDoOutput(true);
        conn.getOutputStream().write(postDataBytes);
        Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (int c; (c = in.read()) >= 0;)
            sb.append((char)c);
        String response = sb.toString();
        System.out.println(response);
        // OR:
//        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//        String inputLine;
//        StringBuffer response = new StringBuffer();
//        while ((inputLine = in.readLine()) != null) {
//            response.append(inputLine);
//        }
//        in.close();

        JSONObject myResponse = new JSONObject(response.toString());

//        System.out.println("result after Reading JSON Response");
//        System.out.println("origin- "+myResponse.getString("origin"));
//        System.out.println("url- "+myResponse.getString("url"));
//        JSONObject form_data = myResponse.getJSONObject("form");
//        System.out.println("CODE- "+form_data.getString("CODE"));
//        System.out.println("email- "+form_data.getString("email"));
//        System.out.println("message- "+form_data.getString("message"));
//        System.out.println("name"+form_data.getString("name"));
    }
}



//        //Example how to handle a call from the server, for example if the server sends:
//        // "SetButtonValue", new_val
//        // Where new_val is a Float value: (commented out since I don really use f;oats anywhere..
//        // so it's just an example
//        hubConnection.on("SetButtonValue",(new_val) -> {
//            sign_up_btn.setText("new_val");
//        }, Float.class);

//        //Example how to handle a call from the server, for example if the server sends:
//        // "SetButtonValue", new_val
//        // Where new_val is a string value:
//        hubConnection.on("SetButtonValue", new Action1<String>() {
//            @Override
//            public void invoke(String new_val) {
//                sign_up_btn.setText(new_val);
//            }
//        }, String.class);
//        //Event handler for button press (from within our app..) that starts or stops the
//        // connection to signalR:
//        sign_up_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(sign_up_btn.getText().toString().toLowerCase().equals("enter pack2school"))
//                {
//                    if (hubConnection.getConnectionState() == HubConnectionState.DISCONNECTED)
//                    {
//                        hubConnection.start();
//                        sign_up_btn.setText("leave");
//                    }
//                }
//                else if (sign_up_btn.getText().toString().toLowerCase().equals("leave"))
//                {
//                    if(hubConnection.getConnectionState() == HubConnectionState.CONNECTED)
//                    {
//                        //Sending something to the connection example:
//                        float param_1 = 1;
//                        float param_2 = 2;
//                        hubConnection.send("SetNewState", param_1, param_2);
//                        hubConnection.stop();
//                        sign_up_btn.setText("enter pack2school");
//                    }
//                }
//            }
//        });