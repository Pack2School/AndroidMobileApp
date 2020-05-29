package com.example.pack2school;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class sign_up_teacher_page extends AppCompatActivity {

    Button lets_go_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_teacher_page);

        lets_go_btn = (Button)findViewById(R.id.lets_go_btn);
        lets_go_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText name_input = (EditText) findViewById(R.id.name_input);
                EditText school_input = (EditText) findViewById(R.id.school_input);
                EditText email_input = (EditText) findViewById(R.id.email_input);

                String name_input_str = name_input.getText().toString();
                String school_input_str = school_input.getText().toString();
                String email_input_str = email_input.getText().toString();

                // TODO: call the correct azure function with this params...
            }
        });
    }
}
