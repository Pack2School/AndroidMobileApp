package com.example.pack2school;

import androidx.appcompat.app.AppCompatActivity;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.microsoft.signalr.Action1;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;


public class student_main_page extends AppCompatActivity {

    Intent myIntent;
    String my_user_id_as_string;
    String my_name_as_string;
    TextView user_id_text_view;
    Button scan_my_bag_btn;
    HubConnection hubConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main_page);

        myIntent = getIntent();
        my_user_id_as_string = myIntent.getStringExtra(MainActivity.USER_ID);
        my_name_as_string = myIntent.getStringExtra(MainActivity.NAME);
        user_id_text_view = (TextView)findViewById(R.id.user_id_text_view);
        user_id_text_view.setText(my_user_id_as_string);
        scan_my_bag_btn = (Button)findViewById(R.id.scan_my_bag_btn);


        //NegotiateSignalROutput negotiate_response = negotiateToSignalR(retrofit, my_user_id_as_string, "5");
//        user_id_text_view.setText(negotiate_response.toString());
//        if (negotiate_response == null){
//            user_id_text_view.setText("Error occurred during negotiate, please see 'run' log for more details.");
//        }
//        hubConnection = HubConnectionBuilder.create(negotiate_response.getUrl()).withAccessTokenProvider(negotiate_response.getAccessToken()).build();
        //hubConnection.start();

//        hubConnection.on("UpdateStudent", new Action1<Object>() {
//            @Override
//            public void invoke(Object param1) {
//                // update the table rows (except row 1)
//                // or for now just print it.....
//                System.out.println("UpdateStudent was invoked!");
//                String params = param1.toString();
//                user_id_text_view.setText(params);
//            }
//        }, Object.class);

        scan_my_bag_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Step 1 - initialize retofit with our azure functions base uri:
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://pack2schoolfunctions.azurewebsites.net/api/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
                NegotiateSignalRInput signalR_input = new NegotiateSignalRInput(my_user_id_as_string,"5");
                Call<NegotiateSignalROutput> signalR_call = jsonPlaceHolderApi.getNegotiateConnectionFromSignalR(signalR_input);
                signalR_call.enqueue(new Callback<NegotiateSignalROutput>() {
                    @Override
                    public void onResponse(Call<NegotiateSignalROutput> call, Response<NegotiateSignalROutput> response) {
                        if (!response.isSuccessful()) {
                            System.out.println("Error occurred in enqueueing a negotiate call. Error code: " + response.code());
                            return;
                        }
                        NegotiateSignalROutput post = response.body();
                        String signalR_access_token = post.getAccessToken();
                        String signalR_url = post.getUrl();
                        String content = "";
                        content += "AccessToken: " + signalR_access_token + "\n";
                        content += "Url: " + signalR_url + "\n";
                        // log the credentials to the 'run' console for debugging if needed:
                        System.out.println("Successfully completed negotiate. Returned: \n" + content);
                        hubConnection =  HubConnectionBuilder.create(signalR_url).withAccessTokenProvider(Single.defer(() -> {return Single.just(signalR_access_token);})).build();
                        hubConnection.start();
                        hubConnection.on("UpdateStudent", new Action1<Object>() {
                            @Override
                            public void invoke(Object param1) {
                                // update the table rows (except row 1)
                                // or for now just print it.....
                                System.out.println("UpdateStudent was invoked!");
                                String params = param1.toString();
                                System.out.println("Input from method: " + params);
                                //user_id_text_view.setText("Input from method: " + params);
                            }
                        }, Object.class);
                    }
                    @Override
                    public void onFailure(Call<NegotiateSignalROutput> call, Throwable t) {
                        System.out.println("Enqueueing a negotiate call failed! Failure message: \n" + t.getMessage());
                    }
                });

//                // Step 2 - initialize a jsonPlaceHolderApi that will get all responses as json:
//                JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
//                // Step 3 - send an http request to the negotiate function in azure and get back the access token:
//                NegotiateSignalRInput signalR_input = new NegotiateSignalRInput(my_user_id_as_string,"5");
//                Call<NegotiateSignalROutput> signalR_call = jsonPlaceHolderApi.getNegotiateConnectionFromSignalR(signalR_input);
//                signalR_call.enqueue(new Callback<NegotiateSignalROutput>() {
//                    @Override
//                    public void onResponse(Call<NegotiateSignalROutput> call, Response<NegotiateSignalROutput> response) {
//                        if (!response.isSuccessful()) {
//                            user_id_text_view.setText("Error occured, error code: " + response.code());
//                            return;
//                        }
//                        NegotiateSignalROutput post = response.body();
//                        String signalR_access_token = post.getAccessToken();
//                        String signalR_url = post.getUrl();
//                        String content = "";
//                        content += "AccessToken: " + signalR_access_token + "\n";
//                        content += "Url: " + signalR_url + "\n";
//                        // log the credentials to the 'run' console for debugging if needed:
//                        System.out.println("Successfully completed negotiate. Returned \n:" + content);
//                        user_id_text_view.append(content);
//                    }
//                    @Override
//                    public void onFailure(Call<NegotiateSignalROutput> call, Throwable t) {
//                        user_id_text_view.setText(t.getMessage());
//                    }
//                });
            }
        });
    }

    public NegotiateSignalROutput negotiateToSignalR(Retrofit retrofit, String UserId, String DeviceId){
        // In java, we cannot access an object from an inner class, so we must make it a final array of size 1:
        final NegotiateSignalROutput[] post = new NegotiateSignalROutput[1];
        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        NegotiateSignalRInput signalR_input = new NegotiateSignalRInput(UserId,DeviceId);
        Call<NegotiateSignalROutput> signalR_call = jsonPlaceHolderApi.getNegotiateConnectionFromSignalR(signalR_input);
        signalR_call.enqueue(new Callback<NegotiateSignalROutput>() {
            @Override
            public void onResponse(Call<NegotiateSignalROutput> call, Response<NegotiateSignalROutput> response) {
                if (!response.isSuccessful()) {
                    System.out.println("Error occurred in enqueueing a negotiate call. Error code: " + response.code());
                    return;
                }
                post[0] = response.body();
                String signalR_access_token = post[0].getAccessToken();
                String signalR_url = post[0].getUrl();
                String content = "";
                content += "AccessToken: " + signalR_access_token + "\n";
                content += "Url: " + signalR_url + "\n";
                // log the credentials to the 'run' console for debugging if needed:
                System.out.println("Successfully completed negotiate. Returned: \n" + content);
            }
            @Override
            public void onFailure(Call<NegotiateSignalROutput> call, Throwable t) {
                System.out.println("Enqueueing a negotiate call failed! Failure message: \n" + t.getMessage());
            }
        });
        System.out.println("Before returnnin: " + post[0]);
        return post[0];
    }
}
