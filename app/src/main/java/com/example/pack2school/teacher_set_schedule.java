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

public class teacher_set_schedule extends AppCompatActivity {

    Intent myIntent;
    String my_user_id_as_string;
    String my_class_name_as_string;
    TextView class_name_text_view;
    ArrayAdapter<String> adapter;
    List<String> sunday_subjects_list = new ArrayList<>();
    List<String> monday_subjects_list = new ArrayList<>();
    List<String> tuesday_subjects_list = new ArrayList<>();
    List<String> wedensday_subjects_list = new ArrayList<>();
    List<String> thursday_subjects_list = new ArrayList<>();
    List<String> friday_subjects_list = new ArrayList<>();
    Button update_schedule_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_set_schedule);

        myIntent = getIntent();
        my_user_id_as_string = myIntent.getStringExtra(MainActivity.USER_ID);
        my_class_name_as_string = myIntent.getStringExtra(MainActivity.CLASSES_IDS);
        class_name_text_view = (TextView)findViewById(R.id.class_name_text_view);
        class_name_text_view.setText("Set a weekly schedule for class " + my_class_name_as_string);

        // Get the subjects of the class - this have to be a non empty list - this is validated before opening this page:
        List<String> list_view_choices = (List<String>) myIntent.getStringArrayListExtra(MainActivity.SUBJECTS);

        // Set an adapter for choosing books in the schedule:
        adapter = new ArrayAdapter<String>(this, R.layout.raw_multi_choise_layout, R.id.multi_choice_text_view, list_view_choices);

        // Set all list views to the same adapter (all have same choices) :
        ListView sunday_books_view = (ListView)findViewById(R.id.sunday_list_view);
        set_list_view_adapter(sunday_books_view, sunday_subjects_list);

        ListView monday_books_view = (ListView)findViewById(R.id.monday_list_view);
        set_list_view_adapter(monday_books_view, monday_subjects_list);

        ListView tuesday_books_view = (ListView)findViewById(R.id.tuesday_list_view);
        set_list_view_adapter(tuesday_books_view, tuesday_subjects_list);

        ListView wedensday_books_view = (ListView)findViewById(R.id.wedensday_list_view);
        set_list_view_adapter(wedensday_books_view, wedensday_subjects_list);

        ListView thursday_books_view = (ListView)findViewById(R.id.thursday_list_view);
        set_list_view_adapter(thursday_books_view, thursday_subjects_list);

        ListView friday_books_view = (ListView)findViewById(R.id.friday_list_view);
        set_list_view_adapter(friday_books_view, friday_subjects_list);

        update_schedule_btn = (Button) findViewById(R.id.update_schedule_btn);
        update_schedule_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Sunday: " + sunday_subjects_list + "\n" +
                        "Monday: " + monday_subjects_list + "\n" +
                        "Tuesday: " + tuesday_subjects_list + "\n" +
                        "Wedensday: " + wedensday_subjects_list + "\n" +
                        "Thursday: " + thursday_subjects_list + "\n" +
                        "Friday: " + friday_subjects_list);
                ScheduleSetter schedule = new ScheduleSetter(sunday_subjects_list,
                        monday_subjects_list,
                        tuesday_subjects_list,
                        wedensday_subjects_list,
                        thursday_subjects_list,
                        friday_subjects_list,
                        my_user_id_as_string,
                        my_class_name_as_string);
                JsonPlaceHolderApi jsonPlaceHolderApi = MainActivity.getRetrofitJsonPlaceHolderApi();
                Call<GenericResponse> set_needed_response = jsonPlaceHolderApi.SetClassSchedule(schedule);
                set_needed_response.enqueue(new Callback<GenericResponse>() {
                    @Override
                    public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                        Tuple set_schedule_result = MainActivity.log_request_errors(response, MainActivity.TEACHER, MainActivity.SET_SCHEDULE);
                        if (set_schedule_result.getSucceeded()){
                            GenericResponse request_response = response.body();
                            System.out.println("Entire response of " + MainActivity.SET_SCHEDULE + ": " + request_response.getData());
                            show_message("Successfully updated schedule");
                        }
                        else{
                            show_message("Error: " + set_schedule_result.getError_message());
                        }
                    }

                    @Override
                    public void onFailure(Call<GenericResponse> call, Throwable t) {
                        String err_message = t.getMessage();
                        show_message("Error: " + err_message);
                        System.out.println("Enqueueing a " + MainActivity.SET_SCHEDULE + ":  call failed! Failure message: \n" + err_message);
                    }
                });
            }
        });
    }

    public void set_list_view_adapter(ListView list_view, List<String> choice_list){
        list_view.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        list_view.setAdapter(adapter);
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selected_item = ((TextView)view).getText().toString();
                if(choice_list.contains(selected_item)){
                    choice_list.remove(selected_item);
                }
                else{
                    choice_list.add(selected_item);
                }
            }
        });
    }

    private void show_message(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
