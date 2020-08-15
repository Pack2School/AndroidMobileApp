package com.example.pack2school;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class teacher_main_page extends AppCompatActivity{

    Intent myIntent;
    String my_user_id_as_string;
    String my_user_name_as_string;
    TextView user_name_text_view;
    TextView user_id_text_view;
    Button create_new_class_btn;
    Button update_tomorrows_books_btn;
    Button checkout_class_status_btn;
    Spinner update_tomorrows_books_spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_main_page);

        myIntent = getIntent();
        my_user_id_as_string = myIntent.getStringExtra(MainActivity.USER_ID);
        my_user_name_as_string = myIntent.getStringExtra(MainActivity.NAME);
        user_name_text_view = (TextView)findViewById(R.id.user_name_text_view);
        user_id_text_view = (TextView)findViewById(R.id.user_id_text_view);

        user_name_text_view.setText("Welcome " + my_user_name_as_string);
        user_id_text_view.setText("User ID: " + my_user_id_as_string + ", Teacher.");

        List<String> classes = new ArrayList<>();
        classes.add("class1");
        classes.add("class2");
        // TODO - instead of manually adding classes - we should take returned classes from the http
        //  response "data" attribute.
        update_tomorrows_books_spinner = (Spinner) findViewById(R.id.selected_class_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, classes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        update_tomorrows_books_spinner.setAdapter(adapter);
        update_tomorrows_books_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
             @Override
             public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                 String str = (String) parent.getSelectedItem();
                 displaySelectedClass(str);
             }

             @Override
             public void onNothingSelected(AdapterView<?> parent) {

             }
        });

        create_new_class_btn = (Button) findViewById(R.id.create_class_btn);
        update_tomorrows_books_btn = (Button)findViewById(R.id.update_tomorrow_btn);
        checkout_class_status_btn = (Button)findViewById(R.id.checkout_class_status_btn);

        create_new_class_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_create_new_class_window();
            }
        });

        update_tomorrows_books_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_update_books_window();
            }
        });

        checkout_class_status_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_checkout_class_status_window();
            }
        });
    }

    public void open_create_new_class_window(){
        // TODO - implement
    }

    public void open_update_books_window(){
        // TODO - implement
    }

    public void open_checkout_class_status_window(){
        // TODO - implement
    }

    public String getSelectedClass(View v, Spinner s) {
        // TODO - use when going over to pages of checkout class/update books
        String class_name = (String) s.getSelectedItem();
        return class_name;
    }

    private void displaySelectedClass(String class_name) {
        String SelectedData = "Selected class: " + class_name;
        Toast.makeText(this, SelectedData, Toast.LENGTH_LONG).show();
    }
}
