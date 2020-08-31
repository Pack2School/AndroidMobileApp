package com.example.pack2school;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class teacher_update_class_books_page extends AppCompatActivity {

    Intent myIntent;
    String my_user_id_as_string;
    String my_class_name_as_string;
    TextView class_name_text_view;
    Button update_tomorrow_btn;

    ArrayList<String> selected_books = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_update_class_books_page);

        myIntent = getIntent();
        my_user_id_as_string = myIntent.getStringExtra(MainActivity.USER_ID);
        my_class_name_as_string = myIntent.getStringExtra(MainActivity.CLASSES_IDS);
        class_name_text_view = (TextView)findViewById(R.id.class_name_text_view);
        class_name_text_view.setText("Update needed books for class " + my_class_name_as_string);

        // Get the subjects of the class:
        List<String> subjects = (List<String>) myIntent.getStringArrayListExtra(MainActivity.SUBJECTS);
        if (subjects == null){
            subjects = new ArrayList<>();
        }

        List<String> list_view_choices = new ArrayList<>();
        for (String choice: subjects){
            list_view_choices.add(choice);
        }

        ListView possible_books = (ListView)findViewById(R.id.multi_choice_list_view);
        possible_books.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.raw_multi_choise_layout, R.id.multi_choice_text_view, list_view_choices);
        possible_books.setAdapter(adapter);
        possible_books.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selected_item = ((TextView)view).getText().toString();
                if(selected_books.contains(selected_item)){
                    selected_books.remove(selected_item);
                }
                else{
                    selected_books.add(selected_item);
                }
            }
        });

        update_tomorrow_btn = (Button) findViewById(R.id.update_tomorrow_btn);
        update_tomorrow_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubjectRequest set_needed = new SubjectRequest(null,
                        my_class_name_as_string,
                        null,
                        null,
                        MainActivity.SET_NEEDED_SUBJECTS,
                        selected_books,
                        null);
                JsonPlaceHolderApi jsonPlaceHolderApi = MainActivity.getRetrofitJsonPlaceHolderApi();
                Call<GenericResponse> set_needed_response = jsonPlaceHolderApi.UpdateSubjectNecessity(set_needed);
                set_needed_response.enqueue(new Callback<GenericResponse>() {
                    @Override
                    public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                        Tuple get_subjects_result = MainActivity.log_request_errors(response, MainActivity.TEACHER, set_needed.getRequestType());
                        if (get_subjects_result.getSucceeded()){
                            GenericResponse request_response = response.body();
                            System.out.println("Entire response of EditSubject: " + request_response.getData());
                        }
                        else{
                            show_message("Error: " + get_subjects_result.getError_message());
                        }
                    }

                    @Override
                    public void onFailure(Call<GenericResponse> call, Throwable t) {
                        String err_message = t.getMessage();
                        show_message("Error: " + err_message);
                        System.out.println("Enqueueing a EditSubject call failed! Failure message: \n" + err_message);
                    }
                });
            }
        });
    }

    private void show_message(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}