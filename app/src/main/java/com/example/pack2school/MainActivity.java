package com.example.pack2school;

import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    // Constants used by methods in all project;
    public static final String USER_TYPE = "userType";
    public static final String USER_ID = "userID";
    public static final String NAME = "userName";
    public static final String DEVICE_CONNECTION_STRING = "deviceConnectionString";
    public static final String SUBJECTS_TABLE_NAME = "subjectsTablesNames";
    public static final String CLASSES_IDS = "classIDs";
    public static final String CHILDREN_IDS = "childrenIDs";
    public static final String INFO = "Info";
    public static final String STUDENT = "Student";
    public static final String TEACHER = "Teacher";
    public static final String PARENT = "Parent";
    public static final String SUBJECTS = "Subjects";
    public static final String NO_CHILDREN_RECEIVED = "There are no children associated with your account.";
    // Operation names:
    public static final String SIGN_UP = "Sign Up";
    public static final String SIGN_IN = "Sign in";
    public static final String SET_SCHEDULE = "Set Schedule";
    public static final String PARENT_CHILD_CHECKUP = "Parent child checkup";
    public static final String SCAN = "Scan";
    public static final String SET_STICKER = "Set Sticker";
    public static final String GET_STUDENT_CLASS_ID = "Get Student class ID";
    public static final String GET_MISSING_SUBJECTS = "Get Missing Subjects";
    public static final String GET_NEEDED_SUBJECTS = "Get Needed Subjects";
    public static final String GET_ALL_SUBJECTS = "Get All Subjects";
    public static final String ADD_NEW_CLASS = "Add new class";
    public static final String SET_NEEDED_SUBJECTS = "Set needed subjects";
    public static final String EDIT_CLASS = "Edit Class";
    // Request types:
    public static final String ADD_SUBJECT = "ADD";
    public static final String RENAME_SUBJECT = "RENAME";
    public static final String DELETE_SUBJECT = "DELETE";
    public static final String ENTRANCE_TYPE = "App Entrance Type";
    public static final String DataBaseAndScanUpdates ="DataBaseAndScanUpdates";

    Button sign_up_btn;
    Button sign_in_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

    // Methods for opening pages from within the main page:

    public void open_sign_up_page(){
        Intent intent = new Intent(this, sign_up_main_page.class);
        startActivity(intent);
    }

    public void open_sign_in_page(){
        Intent intent = new Intent(this, sign_in_main_page.class);
        startActivity(intent);
    }

    // Methods for opening new pages with given parameters:

    public static Intent open_student_main_page(Context ctx, String user_id, String user_name, String device_connection_string, String op_type, String class_name){
        Intent intent = new Intent(ctx, student_main_page.class);
        intent.putExtra(USER_ID, user_id);
        intent.putExtra(NAME, user_name);
        intent.putExtra(ENTRANCE_TYPE, op_type);
        intent.putExtra(DEVICE_CONNECTION_STRING, device_connection_string);
        intent.putExtra(CLASSES_IDS, class_name);
        return intent;
    }

    public static Intent open_teacher_main_page(Context ctx, String user_id, String user_name, ArrayList<String> classes_ids){
        Intent intent = new Intent(ctx, teacher_main_page.class);
        intent.putExtra(USER_ID, user_id);
        intent.putExtra(NAME, user_name);
        intent.putExtra(CLASSES_IDS, classes_ids);
        return intent;
    }

    public static Intent open_parent_main_page(Context ctx, String user_id, String user_name, ArrayList<String> children_ids){
        Intent intent = new Intent(ctx, parent_main_page.class);
        intent.putExtra(USER_ID, user_id);
        intent.putExtra(NAME, user_name);
        intent.putExtra(CHILDREN_IDS, children_ids);
        return intent;
    }

    public static Intent open_teacher_edit_class_page(Context ctx, String teacher_id, String class_name, ArrayList<String> subjects){
        Intent intent = new Intent(ctx, teacher_edit_class_page.class);
        intent.putExtra(USER_ID, teacher_id);
        intent.putExtra(CLASSES_IDS, class_name);
        intent.putExtra(SUBJECTS, subjects);
        return intent;
    }

    public static Tuple<Boolean, String> log_request_errors(Response<GenericResponse> response, String user_type, String operation){
        if (!response.isSuccessful()) {
            System.out.println("Error occurred in " + user_type + " " + operation + ". Error code: " + response.code() + "Error message:" + response.message());
            return new Tuple(false, response.message());
        }
        GenericResponse sign_up_response = response.body();
        String error_message = sign_up_response.getErrorMessage();
        boolean is_request_successful = sign_up_response.didRequestSucceed();
        if (is_request_successful){
            System.out.println(user_type + " " + operation + " was successful.");
            return new Tuple(true, null);
        } else{
            System.out.println(user_type + " " + operation + " was not successful. Error is: " + error_message);
            return new Tuple(false, error_message);
        }
    }

    // SignalR general methods:

    public static Call<NegotiateSignalROutput> get_signalR_connection_call(String user_id){
        JsonPlaceHolderApi jsonPlaceHolderApi = getRetrofitJsonPlaceHolderApi();
        NegotiateSignalRInput signalR_input = new NegotiateSignalRInput(user_id);
        Call<NegotiateSignalROutput> signalR_call = jsonPlaceHolderApi.getNegotiateConnectionFromSignalR(signalR_input);
        return signalR_call;
    }

    public static HubConnection get_hubconnection_from_successful_negotiate_response(Response<NegotiateSignalROutput> response){
        HubConnection hubConnection;
        NegotiateSignalROutput post = response.body();
        String signalR_access_token = post.getAccessToken();
        String signalR_url = post.getUrl();
        // log the credentials to the 'run' console for debugging if needed:
        System.out.println("Successfully completed negotiate. Returned: \n AccessToken: " + signalR_access_token + "\n Url: " + signalR_url);
        hubConnection = HubConnectionBuilder.create(signalR_url).withAccessTokenProvider(Single.defer(() -> {return Single.just(signalR_access_token);})).build();
        return hubConnection;
    }

    // Methods for different api calls:

    public static JsonPlaceHolderApi getRetrofitJsonPlaceHolderApi(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://pack2schoolfunctions.azurewebsites.net/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        return jsonPlaceHolderApi;
    }

    public static void call_backpack_scan_op(String user_id, String user_type){
        JsonPlaceHolderApi jsonPlaceHolderApi = getRetrofitJsonPlaceHolderApi();
        SubjectRequest scan_request = new SubjectRequest(user_id,
                null,
                null,
                null,
                null,
                null,
                null);
        Call<GenericResponse> scan_response = jsonPlaceHolderApi.SendScanOperation(scan_request);
        scan_response.enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                Tuple get_subjects_result = log_request_errors(response, user_type, SCAN);
                if (get_subjects_result.getSucceeded()){
                    System.out.println("Successfully completed scan.");
                }
                else{
                    System.out.println("Error while sending scan command: " + get_subjects_result.getError_message());
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                System.out.println("Enqueueing a SendScanOperation call failed! Failure message: \n" + t.getMessage());
            }
        });
    }

    // General Utils:

    public static boolean are_passwords_aligned(String first, String second){
        if(!first.equals(second)){
            return false;
        }
        return true;
    }

    public static void log_list_items(List<String> list_items, String user_type){
        if(list_items == null){
            System.out.println(user_type + " received an empty list of items.");
            return;
        }
        System.out.println(user_type + " received the following list items: (total of " + list_items.size() + ")\n");
        for(String item: list_items){
            System.out.println(item + "\n");
        }
    }

    public static String order_list_items_to_txt(List<String> str_list){
        String combined = "";
        Collections.sort(str_list);
        if(str_list.size() > 0){
            for(String item: str_list){
                combined = combined + "\n" + item;
            }
        }
        return combined;
    }
}
