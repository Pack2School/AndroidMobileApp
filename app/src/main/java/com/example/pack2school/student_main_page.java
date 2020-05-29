package com.example.pack2school;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.microsoft.signalr.Action1;

public class student_main_page extends AppCompatActivity {

    Intent myIntent;
    int my_user_id;
    String my_user_id_as_string;
    TextView user_id_text_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main_page);
        myIntent = getIntent();
        my_user_id = myIntent.getIntExtra("user_id", -1);
        my_user_id_as_string = String.valueOf(my_user_id);
        user_id_text_view = (TextView)findViewById(R.id.user_id_text_view);
        user_id_text_view.setText(my_user_id_as_string);

        MainActivity.hubConnection.on("updated_books", new Action1<Object>() {
            @Override
            public void invoke(Object param1) {
                //update the table rows (except row 1)
                // or for now just print it.....
            }
        }, Object.class);
    }
}
