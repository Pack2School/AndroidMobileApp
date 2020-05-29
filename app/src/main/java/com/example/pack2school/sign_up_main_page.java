package com.example.pack2school;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class sign_up_main_page extends AppCompatActivity {

    Button sign_up_student_btn;
    Button sign_up_teacher_btn;
    Button sign_up_parent_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_main_page);

        sign_up_student_btn = (Button)findViewById(R.id.Student);
        sign_up_teacher_btn = (Button)findViewById(R.id.Teacher);
        sign_up_parent_btn = (Button)findViewById(R.id.Parent);

        sign_up_student_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_student_sign_up_page();
            }
        });

        sign_up_teacher_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_teacher_sign_up_page();
            }
        });

        sign_up_parent_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_parent_sign_up_page();
            }
        });
    }

    public void open_student_sign_up_page(){
        Intent intent = new Intent(this, sign_up_student_page.class);
        startActivity(intent);
    }

    public void open_teacher_sign_up_page(){
        Intent intent = new Intent(this, sign_up_teacher_page.class);
        startActivity(intent);
    }

    public void open_parent_sign_up_page(){
        Intent intent = new Intent(this, sign_up_parent_page.class);
        startActivity(intent);
    }
}
